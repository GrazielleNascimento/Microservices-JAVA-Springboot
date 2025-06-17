package br.com.msappointment.api.appointment.service;

import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.api.appointment.dto.AppointmentPendingDTO;
import br.com.msappointment.api.appointment.dto.factory.AppointmentDTOFactory;
import br.com.msappointment.api.appointment.model.AppointmentModel;
import br.com.msappointment.api.appointment.model.AppointmentPendingModel;
import br.com.msappointment.api.appointment.model.AppointmentStatusEnum;
import br.com.msappointment.api.appointment.model.AppointmentTypeEnum;
import br.com.msappointment.api.appointment.model.factory.AppointmentModelFactory;
import br.com.msappointment.api.appointment.model.factory.AppointmentPendingDTOFactory;
import br.com.msappointment.api.appointment.model.factory.AppointmentPendingModelFactory;
import br.com.msappointment.api.appointment.repository.AppointmentPendingRepository;
import br.com.msappointment.api.appointment.repository.AppointmentRepository;
import br.com.msappointment.api.exception.ExternalApiException;
import br.com.msappointment.integrations.rabbitmq.dto.PetDTO;
import br.com.msappointment.integrations.rabbitmq.dto.factory.PetDTOFactory;
import br.com.msappointment.integrations.rabbitmq.service.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentPendingRepository appointmentPendingRepository;

    private final RabbitMQService rabbitMQService;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            AppointmentPendingRepository appointmentPendingRepository,
            RabbitMQService rabbitMQService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentPendingRepository = appointmentPendingRepository;
        this.rabbitMQService = rabbitMQService;
    }

    public void createAutomaticAppointmentsForNewPetCreated(PetDTO petDTO) {
        logger.info("Creating an automatic appointment for pet with ID: {}", petDTO.getId());

        // montando os agendamentos iniciais para o pet de acordo com as regras
        List<AppointmentModel> initialAppointments = matchAppointmentsForNewPet(petDTO);

        // salvando cada um dos agendamentos iniciais no banco
        appointmentRepository.saveAll(initialAppointments);

        // enviando mensagem para queue de appointment created
        initialAppointments.stream().forEach(appointmentModel -> {
            rabbitMQService.sendMessageAppointmentStatus(AppointmentDTOFactory.createAppointmentDTOFromAppointmentModel(appointmentModel));
        });
    }

    private List<AppointmentModel> matchAppointmentsForNewPet(PetDTO petDTO) {
        List<AppointmentModel> appointments = new ArrayList<>();

        long petMonths = ChronoUnit.MONTHS.between(petDTO.getBirthDate(), LocalDate.now());
        long petYears = petMonths / 12; // Convertendo meses para anos

        // Banho grátis para todos os pets novos
        appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                petDTO,
                AppointmentTypeEnum.FIRST_BATH_FREE,
                LocalDateTime.now().plusDays(7)) // uma semana
        );

        if (petMonths < 6) {
            // Filhotes recebem primeira vacina
            appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                    petDTO,
                    AppointmentTypeEnum.FIRST_VACCINE,
                    LocalDateTime.now().plusDays(7)) // uma semana
            );
        } else if (petMonths >= 6) {
            // Pets com 6 meses ou mais recebem check-up inicial e vacinação
            appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                    petDTO,
                    AppointmentTypeEnum.INITIAL_CHECKUP,
                    LocalDateTime.now().plusDays(7)) // uma semana
            );
            appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                    petDTO,
                    AppointmentTypeEnum.VACCINATIONS,
                    LocalDateTime.now().plusDays(7)) // uma semana
            );

            // Castração para pets entre 6 e 12 meses
            if (petMonths >= 6 && petMonths < 12) {
                appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                        petDTO,
                        AppointmentTypeEnum.SPAY_NEUTER,
                        LocalDateTime.now().plusDays(7)) // uma semana
                );
            }
        }

        // Pets idosos (7 anos ou mais) recebem consultas veterinárias semestrais
        if (petYears >= 7) {
            appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                    petDTO,
                    AppointmentTypeEnum.VETERINARY_CONSULTATIONS,
                    LocalDateTime.now().plusMonths(6)) // Agendamento semestral
            );
        }

        // Check-up anual completo para pets com 1 ano ou mais
        if (petYears >= 1) {
            appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                    petDTO,
                    AppointmentTypeEnum.BLOOD_TESTS,
                    LocalDateTime.now().plusMonths(12)) // Agendamento anual
            );
            appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                    petDTO,
                    AppointmentTypeEnum.URINE_TESTS,
                    LocalDateTime.now().plusMonths(12)) // Agendamento anual
            );
            appointments.add(AppointmentModelFactory.createInitialAppointmentModelForNewPetCreated(
                    petDTO,
                    AppointmentTypeEnum.X_RAYS,
                    LocalDateTime.now().plusMonths(12)) // Agendamento anual
            );
        }

        return appointments;
    }


    public AppointmentPendingDTO createManualAppointment(AppointmentDTO appointmentDTO) {
        logger.info("Creating a manual appointment for pet with ID: {}", appointmentDTO.getPetId());
        try {
            // criando um id de correlacao para garantir que a resposta do pet seja para o agendamento correto
            UUID correlationaId = UUID.randomUUID(); // id de correlacao para garantir que a resposta do pet seja para o agendamento correto
            AppointmentPendingModel appointmentPending = AppointmentPendingModelFactory
                    .createAppointmentPendingModelFromAppointmentDTO(appointmentDTO, correlationaId);

            // salvando o agendamento pendente no banco pois nao temos a informacao do pet ainda para finalizar o agendamento
            appointmentPendingRepository.save(appointmentPending);

            // solicitando informacoes do pet para o microservico do pet na fila de pet.info.request
            rabbitMQService.requestPetInfo(PetDTOFactory.createPetDTOFromApointmentPending(appointmentPending));

            // retornando o agendamento pendente para o cliente. o agendamento nao existe ainda. o agendamento completo
            // sera criado quando o pet responder com as informacoes solicitadas no metodo finalizePendingAppointmentWithPetInfo()
            logger.info("Appointment pending with correlationalId: {} created successfully", appointmentPending.getCorrelationId());
            return AppointmentPendingDTOFactory.createAppointmentPendingDTOFromAppointmentPendingModel(appointmentPending);
        } catch (Exception e) {
            logger.error("Error creating appointment", e);
            throw new ExternalApiException("Error creating appointment", e);
        }
    }

    public List<AppointmentDTO> getAllAppointments() {
        logger.info("Fetching all appointments");
        return appointmentRepository.findAll().stream()
                .map(AppointmentDTOFactory::createAppointmentDTOFromAppointmentModel)
                .collect(Collectors.toList());
    }


    public void finalizePendingAppointmentWithPetInfo(PetDTO petDTO) {
        logger.info("Finalizing pending appointment with pet info for pet with correlationalId: {}", petDTO.getId());

        // consultando o agendamento pendente pelo correlatioId da info solicitada que o pet respondeu
        Optional<AppointmentPendingModel> opAppointmentPending = appointmentPendingRepository.findById(petDTO.getCorrelationId());
        if (opAppointmentPending.isEmpty()) {
            logger.error("Appointment pending not found for correlationalId: {}", petDTO.getCorrelationId());
            return;
        }

        AppointmentPendingModel appointmentPending = opAppointmentPending.get();

        AppointmentDTO appointmentDTO = AppointmentDTOFactory.createAppointmentDTOFromAppointmentPendingDTO(appointmentPending, petDTO);

        // criando o agendamento no banco
        createAppointment(appointmentDTO);

        // deletando o agendamento que estava pendente
        appointmentPendingRepository.deleteById(appointmentPending.getCorrelationId());

        // enviando mensagem para queue de appointment created
        rabbitMQService.sendMessageAppointmentStatus(appointmentDTO);
    }

    public void createAppointment(AppointmentDTO appointmentDTO) {
        logger.info("Creating an appointment for pet with ID: {}", appointmentDTO.getPetId());
        try {
            AppointmentModel appointment = AppointmentModelFactory.createAppointmentModelFromAppointmentDTO(appointmentDTO);
            appointment = appointmentRepository.save(appointment);
            logger.info("Appointment with ID: {} created successfully", appointment.getId());
        } catch (Exception e) {
            logger.error("Error creating appointment", e);
            throw new ExternalApiException("Error creating appointment", e);
        }
    }

    public AppointmentDTO confirmAppointment(Integer appointmentId) {
        logger.info("Confirming appointment with ID: {}", appointmentId);
        try {
            AppointmentModel appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ExternalApiException("Appointment not found", new RuntimeException()));

            appointment.setStatus(AppointmentStatusEnum.CONFIRMED);
            appointment = appointmentRepository.save(appointment);

            AppointmentDTO confirmedAppointment = AppointmentDTOFactory.createAppointmentDTOFromAppointmentModel(appointment);
            rabbitMQService.sendMessageAppointmentStatus(confirmedAppointment);

            return confirmedAppointment;
        } catch (Exception e) {
            logger.error("Error confirming appointment", e);
            throw new ExternalApiException("Error confirming appointment", e);
        }
    }

//    public AppointmentDTO rescheduleAppointment(Integer appointmentId, LocalDateTime newDate) {
//        logger.info("Rescheduling appointment ID: {} to date: {}", appointmentId, newDate);
//        try {
//            AppointmentModel appointment = appointmentRepository.findById(appointmentId)
//                    .orElseThrow(() -> new ExternalApiException("Appointment not found", new RuntimeException()));
//
//            appointment.setDateAppointment(newDate);
//            appointment.setStatus(AppointmentStatusEnum.RESCHEDULED);
//            appointment = appointmentRepository.save(appointment);
//
//            AppointmentDTO rescheduledAppointment = AppointmentDTOFactory.createAppointmentDTOFromAppointmentModel(appointment);
//            rabbitMQService.sendMessageAppointmentStatus(rescheduledAppointment);
//
//            return rescheduledAppointment;
//        } catch (Exception e) {
//            logger.error("Error rescheduling appointment", e);
//            throw new ExternalApiException("Error rescheduling appointment", e);
//        }
//    }

    public void cancelAppointment(Integer appointmentId) {
        logger.info("Canceling appointment with ID: {}", appointmentId);

            AppointmentModel appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ExternalApiException("Appointment not found", new RuntimeException()));

            appointment.setStatus(AppointmentStatusEnum.CANCELLED);
            appointmentRepository.save(appointment);

            rabbitMQService.sendMessageAppointmentStatus(AppointmentDTOFactory.createAppointmentDTOFromAppointmentModel(appointment));

    }
    /**
     * Filtra agendamentos com base nos critérios fornecidos
     * @param appointmentId ID do agendamento (opcional)
     * @param petId ID do pet (opcional)
     * @param petName Nome do pet (opcional)
     * @param status Status do agendamento (opcional)
     * @param fromDate Data inicial (opcional)
     * @param toDate Data final (opcional)
     * @param careType Tipo de cuidado (opcional)
     * @return Lista de agendamentos filtrados
     */
    public List<AppointmentDTO> filterAppointments(
            Integer appointmentId,
            Integer petId,
            String petName,
            AppointmentStatusEnum status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String careType) {

        // Registra o início da operação de filtragem com todos os parâmetros recebidos
        logger.info("Iniciando filtragem de agendamentos com os seguintes parâmetros - appointmentId: {}, petId: {}, petName: {}, status: {}, fromDate: {}, toDate: {}, careType: {}",
                appointmentId, petId, petName, status, fromDate, toDate, careType);

        // FLUXO: 1. Busca todos os agendamentos do banco de dados
        logger.debug("Buscando todos os agendamentos no banco de dados");
        List<AppointmentModel> appointments = appointmentRepository.findAll();
        logger.info("Total de agendamentos encontrados antes da filtragem: {}", appointments.size());

        // FLUXO: 2. Prepara o stream para aplicação de filtros em cascata
        logger.debug("Preparando stream para aplicação de filtros");
        Stream<AppointmentModel> filteredStream = appointments.stream();
        int totalFilters = 0;

        // FLUXO: 3. Aplica filtro por ID do agendamento, se fornecido
        if (appointmentId != null) {
            logger.debug("Aplicando filtro por ID do agendamento: {}", appointmentId);
            filteredStream = filteredStream.filter(a -> a.getId().equals(appointmentId));
            totalFilters++;
        }

        // FLUXO: 4. Aplica filtro por ID do pet, se fornecido
        if (petId != null) {
            logger.debug("Aplicando filtro por ID do pet: {}", petId);
            filteredStream = filteredStream.filter(a -> a.getPetId().equals(petId));
            totalFilters++;
        }

        // FLUXO: 5. Aplica filtro por nome do pet, se fornecido
        if (petName != null && !petName.trim().isEmpty()) {
            logger.debug("Aplicando filtro por nome do pet: {}", petName);
            // Usando contains para busca parcial, ignorando maiúsculas/minúsculas
            filteredStream = filteredStream.filter(a ->
                    a.getPetName() != null &&
                            a.getPetName().toLowerCase().contains(petName.toLowerCase()));
            totalFilters++;
        }

        // FLUXO: 6. Aplica filtro por status, se fornecido
        if (status != null) {
            logger.debug("Aplicando filtro por status do agendamento: {}", status);
            filteredStream = filteredStream.filter(a -> a.getStatus().equals(status));
            totalFilters++;
        }

        // FLUXO: 7. Aplica filtro por data inicial, se fornecida
        if (fromDate != null) {
            logger.debug("Aplicando filtro por data inicial: {}", fromDate);
            filteredStream = filteredStream.filter(a -> !a.getDateAppointment().isBefore(fromDate));
            totalFilters++;
        }

        // FLUXO: 8. Aplica filtro por data final, se fornecida
        if (toDate != null) {
            logger.debug("Aplicando filtro por data final: {}", toDate);
            filteredStream = filteredStream.filter(a -> !a.getDateAppointment().isAfter(toDate));
            totalFilters++;
        }

        // FLUXO: 9. Aplica filtro por tipo de cuidado, se fornecido
        if (careType != null && !careType.isEmpty()) {
            try {
                logger.debug("Tentando converter string '{}' para enum AppointmentTypeEnum", careType);
                AppointmentTypeEnum careTypeEnum = AppointmentTypeEnum.valueOf(careType);
                logger.debug("Aplicando filtro por tipo de cuidado: {}", careTypeEnum);
                filteredStream = filteredStream.filter(a -> a.getCareType().equals(careTypeEnum));
                totalFilters++;
            } catch (IllegalArgumentException e) {
                logger.warn("Tipo de cuidado inválido ignorado: '{}'. Erro: {}", careType, e.getMessage());
                // Ignora o filtro se o tipo de cuidado for inválido, mas mantém o processamento
            }
        }

        // FLUXO: 10. Converte os modelos para DTOs
        logger.debug("Convertendo modelos de agendamento para DTOs");
        List<AppointmentDTO> result = filteredStream
                .map(AppointmentDTOFactory::createAppointmentDTOFromAppointmentModel)
                .collect(Collectors.toList());

        // FLUXO: 11. Registra resultados da operação
        logger.info("Filtragem concluída. Total de filtros aplicados: {}. Agendamentos encontrados: {} de {} totais.",
                totalFilters, result.size(), appointments.size());

        return result;
    }

    public AppointmentDTO rescheduleAppointment(Integer appointmentId, LocalDateTime newDate) {
        logger.info("Iniciando reagendamento - ID: {}, Nova data recebida: {}", appointmentId, newDate);

        try {
            // Busca o agendamento
            AppointmentModel appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ExternalApiException("Appointment not found", new RuntimeException()));

            logger.info("Agendamento encontrado. Data atual: {}", appointment.getDateAppointment());

            // SOLUÇÃO: Criar um novo objeto LocalDateTime mantendo exatamente a hora informada
            LocalDateTime fixedDateTime = LocalDateTime.of(
                    newDate.getYear(),
                    newDate.getMonthValue(),
                    newDate.getDayOfMonth(),
                    newDate.getHour(),
                    newDate.getMinute(),
                    0
            );

            logger.info("Data original: {}, Nova data criada: {}", newDate, fixedDateTime);

            // Atualiza o agendamento
            appointment.setDateAppointment(fixedDateTime);
            appointment.setStatus(AppointmentStatusEnum.RESCHEDULED);

            logger.info("Salvando agendamento com data: {}", appointment.getDateAppointment());

            // Salva no banco
            appointment = appointmentRepository.save(appointment);

            logger.info("Agendamento salvo. Data após salvar: {}", appointment.getDateAppointment());

            // Converte para DTO e notifica
            AppointmentDTO rescheduledAppointment = AppointmentDTOFactory.createAppointmentDTOFromAppointmentModel(appointment);
            rabbitMQService.sendMessageAppointmentStatus(rescheduledAppointment);

            return rescheduledAppointment;
        } catch (Exception e) {
            logger.error("Erro ao reagendar agendamento", e);
            throw new ExternalApiException("Error rescheduling appointment", e);
        }
    }
}



//package br.com.msappointment.api.util;
//
//import br.com.msappointment.api.appointment.dto.AppointmentDTO;
//import br.com.msappointment.api.appointment.dto.factory.AppointmentDTOFactory;
//import br.com.msappointment.api.appointment.model.AppointmentModel;
//import br.com.msappointment.api.appointment.model.AppointmentStatusEnum;
//import br.com.msappointment.api.appointment.model.AppointmentTypeEnum;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * Utilitário para filtrar agendamentos
// */
//public class AppointmentFilterUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(AppointmentFilterUtil.class);
//
//    /**
//     * Filtra agendamentos com base nos critérios fornecidos
//     * @param appointments Lista de modelos de agendamento a serem filtrados
//     * @param appointmentId ID do agendamento (opcional)
//     * @param petId ID do pet (opcional)
//     * @param petName Nome do pet (opcional)
//     * @param status Status do agendamento (opcional)
//     * @param fromDate Data inicial (opcional)
//     * @param toDate Data final (opcional)
//     * @param careType Tipo de cuidado (opcional)
//     * @return Lista de DTOs de agendamentos filtrados
//     */
//    public static List<AppointmentDTO> filterAppointments(
//            List<AppointmentModel> appointments,
//            Integer appointmentId,
//            Integer petId,
//            String petName,
//            AppointmentStatusEnum status,
//            LocalDateTime fromDate,
//            LocalDateTime toDate,
//            String careType) {
//
//        try {
//            if (appointments == null) {
//                logger.warn("Lista de agendamentos nula recebida para filtragem");
//                return new ArrayList<>();
//            }
//
//            logger.debug("Iniciando filtragem de {} agendamentos", appointments.size());
//
//            // Preparando stream para filtragem em cascata
//            Stream<AppointmentModel> filteredStream = appointments.stream();
//            int totalFilters = 0;
//
//            // Filtro por ID do agendamento
//            if (appointmentId != null) {
//                logger.debug("Aplicando filtro por ID: {}", appointmentId);
//                filteredStream = filteredStream.filter(a -> a.getId().equals(appointmentId));
//                totalFilters++;
//            }
//
//            // Filtro por ID do pet
//            if (petId != null) {
//                logger.debug("Aplicando filtro por ID do pet: {}", petId);
//                filteredStream = filteredStream.filter(a -> a.getPetId().equals(petId));
//                totalFilters++;
//            }
//
//            // Filtro por nome do pet (parcial, case insensitive)
//            if (petName != null && !petName.trim().isEmpty()) {
//                logger.debug("Aplicando filtro por nome do pet: {}", petName);
//                filteredStream = filteredStream.filter(a ->
//                        a.getPetName() != null &&
//                                a.getPetName().toLowerCase().contains(petName.toLowerCase())
//                );
//                totalFilters++;
//            }
//
//            // Filtro por status
//            if (status != null) {
//                logger.debug("Aplicando filtro por status: {}", status);
//                filteredStream = filteredStream.filter(a -> a.getStatus().equals(status));
//                totalFilters++;
//            }
//
//            // Filtro por data inicial
//            if (fromDate != null) {
//                logger.debug("Aplicando filtro por data inicial: {}", fromDate);
//                filteredStream = filteredStream.filter(a -> !a.getDateAppointment().isBefore(fromDate));
//                totalFilters++;
//            }
//
//            // Filtro por data final
//            if (toDate != null) {
//                logger.debug("Aplicando filtro por data final: {}", toDate);
//                filteredStream = filteredStream.filter(a -> !a.getDateAppointment().isAfter(toDate));
//                totalFilters++;
//            }
//
//            // Filtro por tipo de cuidado
//            if (careType != null && !careType.isEmpty()) {
//                try {
//                    AppointmentTypeEnum careTypeEnum = AppointmentTypeEnum.valueOf(careType.toUpperCase());
//                    logger.debug("Aplicando filtro por tipo de cuidado: {}", careTypeEnum);
//                    filteredStream = filteredStream.filter(a -> a.getCareType().equals(careTypeEnum));
//                    totalFilters++;
//                } catch (IllegalArgumentException e) {
//                    logger.warn("Tipo de cuidado inválido ignorado: '{}'. Erro: {}", careType, e.getMessage());
//                    // Ignora filtro inválido e continua processando
//                }
//            }
//
//            // Converte modelos para DTOs e coleta em uma lista
//            List<AppointmentDTO> result = filteredStream
//                    .map(appointment -> {
//                        try {
//                            return AppointmentDTOFactory.createAppointmentDTOFromAppointmentModel(appointment);
//                        } catch (Exception e) {
//                            // Registra erro mas não interrompe o processamento dos outros itens
//                            logger.error("Erro ao converter model para DTO para o agendamento ID: {}. Erro: {}",
//                                    appointment.getId(), e.getMessage());
//                            return null;
//                        }
//                    })
//                    .filter(dto -> dto != null) // Remove possíveis nulos da conversão
//                    .collect(Collectors.toList());
//
//            logger.info("Filtragem concluída. Total de filtros aplicados: {}. Agendamentos encontrados: {} de {} totais.",
//                    totalFilters, result.size(), appointments.size());
//
//            return result;
//        } catch (Exception e) {
//            logger.error("Erro durante a filtragem de agendamentos", e);
//            // Retorna lista vazia em caso de erro para evitar falha completa
//            return new ArrayList<>();
//        }
//    }
//}
package br.com.msappointment.api.appointment.controller;

import br.com.msappointment.api.Response;
import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.api.appointment.dto.AppointmentPendingDTO;
import br.com.msappointment.api.appointment.dto.AppointmentRescheduleDTO;
import br.com.msappointment.api.appointment.model.AppointmentStatusEnum;
import br.com.msappointment.api.appointment.service.AppointmentService;
import br.com.msappointment.api.exception.ExternalApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointment")
@Tag(name = "Appointment System", description = "Operations pertaining to care scheduling")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);


    @PostMapping("/manual")
    @Operation(summary = "Create a manual appointment", description = "Create a manual appointment with the provided details", tags = { "Appointment System" })
    public ResponseEntity<Response<AppointmentPendingDTO>> createManualAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentPendingDTO appointmentPending = appointmentService.createManualAppointment(appointmentDTO);

        Response response = new Response();
        if (appointmentPending == null) {
            response.setErrors("Error creating appointment pending");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.setData(appointmentPending);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all appointments", description = "Retrieve a list of all appointments", tags = { "Appointment System" })
    public ResponseEntity<Response<List<AppointmentDTO>>> getAllAppointments() {
        logger.info("New request to get all appointments");
        List<AppointmentDTO> appointmentDTOS = appointmentService.getAllAppointments();

        Response response = new Response();
        if (appointmentDTOS.isEmpty()) {
            response.setErrors("No appointments found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(appointmentDTOS);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiException(ExternalApiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("External API error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm appointment", description = "Confirm a scheduled appointment", tags = { "Appointment System" })
    public ResponseEntity<Response<AppointmentDTO>> confirmAppointment(@PathVariable("id") Integer id) {
        Response<AppointmentDTO> response = new Response<>();
        try {
            AppointmentDTO confirmedAppointment = appointmentService.confirmAppointment(id);
            response.setData(confirmedAppointment);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            response.setErrors(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule appointment", description = "Reschedule an existing appointment", tags = { "Appointment System" })
    public ResponseEntity<Response<AppointmentDTO>> rescheduleAppointment(
            @PathVariable("id") Integer id,
            @RequestBody AppointmentRescheduleDTO request) {
        Response<AppointmentDTO> response = new Response<>();
        try {
            AppointmentDTO rescheduledAppointment = appointmentService.rescheduleAppointment(id, request.getNewDate());
            response.setData(rescheduledAppointment);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            response.setErrors(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment", tags = { "Appointment System" })
    public ResponseEntity<Response<Void>> cancelAppointment(@PathVariable("id")  Integer id) {
        Response<Void> response = new Response<>();
        try {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            response.setErrors(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter appointments",
            description = "Filter appointments by appointment ID, pet ID, pet name, status, date range, and care type",
            tags = { "Appointment System" })
    public ResponseEntity<Response<List<AppointmentDTO>>> filterAppointments(
            @RequestParam(value = "appointmentId", required = false) Integer appointmentId,

            @RequestParam(value = "petId", required = false) Integer petId,

            @RequestParam(value = "petName", required = false) String petName,

            @RequestParam(value = "status", required = false) AppointmentStatusEnum status,

            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,

            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,

            @RequestParam(value = "careType", required = false) String careType) {



        // Registra o início da requisição com todos os parâmetros recebidos
        logger.info("Nova requisição para filtrar agendamentos com parâmetros: appointmentId={}, petId={}, petName={}, status={}, fromDate={}, toDate={}, careType={}",
                appointmentId, petId, petName, status, fromDate, toDate, careType);

        // FLUXO: 1. Delega a filtragem para o serviço
        List<AppointmentDTO> appointmentDTOS = appointmentService.filterAppointments(
                appointmentId, petId, petName, status, fromDate, toDate, careType);

        // FLUXO: 2. Prepara a resposta
        Response<List<AppointmentDTO>> response = new Response<>();

        // FLUXO: 3. Verifica se foram encontrados resultados
        if (appointmentDTOS.isEmpty()) {
            // FLUXO: 3a. Caso não tenha encontrado agendamentos, retorna NOT_FOUND com mensagem apropriada
            logger.info("Nenhum agendamento encontrado com os filtros aplicados");
            response.setErrors("Nenhum agendamento encontrado com os filtros aplicados");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // FLUXO: 3b. Caso tenha encontrado agendamentos, retorna OK com a lista
        logger.info("Filtro de agendamentos concluído com sucesso. Total de agendamentos encontrados: {}", appointmentDTOS.size());
        response.setData(appointmentDTOS);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/testhorario")
    @Operation(summary = "Teste de horário", description = "Teste de horário", tags = { "Appointment System" })
    public ResponseEntity<LocalDateTime> testHorario() {
        return ResponseEntity.ok(LocalDateTime.now());
    }
}



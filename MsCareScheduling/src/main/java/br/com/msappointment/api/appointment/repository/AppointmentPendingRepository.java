package br.com.msappointment.api.appointment.repository;

import br.com.msappointment.api.appointment.model.AppointmentPendingModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentPendingRepository extends JpaRepository<AppointmentPendingModel, UUID> {
}

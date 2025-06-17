package br.com.msappointment.api.appointment.repository;

import br.com.msappointment.api.appointment.model.AppointmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentModel, Integer> {
}
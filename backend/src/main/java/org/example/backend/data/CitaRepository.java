package org.example.backend.data;

import org.example.backend.logic.Cita;
import org.example.backend.logic.Medico;
import org.example.backend.logic.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findCitaByMedico(Medico medico);
    List<Cita> findCitaByPacienteOrderByFechaCitaDescHoraCitaDesc(Paciente paciente);
    List<Cita> findCitaByMedicoOrderByFechaCitaDescHoraCitaDesc(Medico doctor);
    List<LocalTime> findLocalTimeByMedico(Medico medico);
    List<Cita> findAllByFechaCitaBetween(LocalDate inicio, LocalDate fin);

}

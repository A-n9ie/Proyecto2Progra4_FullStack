package org.example.backend.logic;

import org.example.backend.data.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service("serviceAppointment")
public class ServiceAppointment {
    @Autowired
    private CitaRepository citaRepository;

    public List<Cita> citasFindAll() {
        return citaRepository.findAll();
    }

    public void saveAppointment(Cita cita) {
        citaRepository.save(cita);
    }

    public void deleteAppointment(Cita cita) {citaRepository.delete(cita);}

    public List<Cita> citasPaciente(Paciente paciente){return citaRepository.findCitaByPacienteOrderByFechaCitaDescHoraCitaDesc(paciente);}

    public List<Cita> citasMedico(Medico doctor){return citaRepository.findCitaByMedicoOrderByFechaCitaDescHoraCitaDesc(doctor);}

    public List<Cita> citasPacienteFiltradas(Paciente paciente, String status, String doctor) {
        List<Cita> citas = citasPaciente(paciente);
        if ((status == null || status.isEmpty()) && (doctor == null || doctor.isEmpty())) {
            return citas;
        }

        return citas.stream()
                .filter(c -> {
                    boolean matchesStatus = true;
                    boolean matchesPatient = true;

                    if (status != null && !status.isEmpty()) {
                        matchesStatus = c.getEstado().toLowerCase().contains(status.toLowerCase());
                    }

                    if (doctor != null && !doctor.isEmpty()) {
                        matchesPatient = c.getMedico().getNombre().toLowerCase().contains(doctor.toLowerCase());
                    }

                    return matchesStatus && matchesPatient;
                })
                .collect(Collectors.toList());
    }

    public List<Cita> citasMedicoFiltradas(Medico medico, String status, String patient) {
        List<Cita> citas = citasMedico(medico);

        if ((status == null || status.isEmpty()) && (patient == null || patient.isEmpty())) {
            return citas;
        }

        return citas.stream()
                .filter(c -> {
                    boolean matchesStatus = true;
                    boolean matchesPatient = true;

                    if (status != null && !status.isEmpty()) {
                        matchesStatus = c.getEstado().toLowerCase().contains(status.toLowerCase());
                    }

                    if (patient != null && !patient.isEmpty()) {
                        matchesPatient = c.getPaciente().getNombre().toLowerCase().contains(patient.toLowerCase());
                    }

                    return matchesStatus && matchesPatient;
                })
                .collect(Collectors.toList());
    }


    public Cita getCitaById(Integer show) {
        return citaRepository.getReferenceById(show);
    }
}
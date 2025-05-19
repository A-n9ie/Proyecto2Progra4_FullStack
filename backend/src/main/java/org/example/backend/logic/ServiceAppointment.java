package org.example.backend.logic;

import org.example.backend.data.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

    public List<Cita> citasPacienteFiltradas(Paciente paciente, String estado, String doctor) {
        List<Cita> citasPaciente = citasPaciente(paciente);
        List<Cita> filtrada = new ArrayList<>();
        if ("All".equalsIgnoreCase(estado) && !doctor.isEmpty()) {
            for(Cita c: citasPaciente)
                if(c.getMedico().getNombre().toLowerCase().contains(doctor.toLowerCase()))
                    filtrada.add(c);
            return filtrada;
        }
        if (doctor.isEmpty() && !estado.isEmpty()){
            for(Cita c: citasPaciente)
                if(c.getEstado().toLowerCase().contains(estado.toLowerCase()))
                    filtrada.add(c);
            return filtrada;
        }
        for(Cita c: citasPaciente)
            if(c.getMedico().getNombre().toLowerCase().contains(doctor.toLowerCase()) &&
                    c.getEstado().toLowerCase().contains(estado.toLowerCase()))
                filtrada.add(c);
        return filtrada;
    }
    public List<Cita> citasMedicoFiltradas(Medico medico, String estado, String paciente) {
        List<Cita> citasPaciente = citasMedico(medico);
        List<Cita> filtrada = new ArrayList<>();
        if ("All".equalsIgnoreCase(estado) && !paciente.isEmpty()) {
            for(Cita c: citasPaciente)
                if(c.getPaciente().getNombre().toLowerCase().contains(paciente.toLowerCase()))
                    filtrada.add(c);
            return filtrada;
        }
        if (paciente.isEmpty() && !estado.isEmpty()){
            for(Cita c: citasPaciente)
                if(c.getEstado().toLowerCase().contains(estado.toLowerCase()))
                    filtrada.add(c);
            return filtrada;
        }
        for(Cita c: citasPaciente)
            if(c.getPaciente().getNombre().toLowerCase().contains(paciente.toLowerCase()) &&
                    c.getEstado().toLowerCase().contains(estado.toLowerCase()))
                filtrada.add(c);
        return filtrada;
    }

    public Cita getCitaById(Integer show) {
        return citaRepository.getReferenceById(show);
    }
}
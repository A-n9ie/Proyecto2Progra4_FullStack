package org.example.backend.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.backend.logic.HorariosMedico;
import org.example.backend.logic.Medico;

import java.util.List;

public class EditMedicoHorarioDTO {
    private Medico medico;
    private List<HorariosMedicosDTO> horarios;

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public List<HorariosMedicosDTO> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorariosMedicosDTO> horarios) {
        this.horarios = horarios;
    }
}

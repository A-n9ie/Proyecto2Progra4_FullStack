package org.example.backend.logic;

import java.util.List;

public class MedicosConHorarios {
    private Integer medicoId;
    private List<HorariosMedico> horarios;

    public MedicosConHorarios(Integer medicoId, List<HorariosMedico> horarios) {
        this.medicoId = medicoId;
        this.horarios = horarios;
    }

    public Integer getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

    public List<HorariosMedico> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorariosMedico> horarios) {
        this.horarios = horarios;
    }
}

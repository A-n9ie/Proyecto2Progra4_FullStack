package org.example.backend.DTO;

import java.time.LocalTime;

public class HorariosMedicosDTO {
    private Integer medicoId;
    private String diaSemana;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private Integer frecuencia;

    public HorariosMedicosDTO(Integer medicoId, String diaSemana, LocalTime horarioInicio, LocalTime horarioFin, Integer frecuencia) {
        this.medicoId = medicoId;
        this.diaSemana = diaSemana;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.frecuencia = frecuencia;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public Integer getFrecuenciaCitas() {
        return frecuencia;
    }

    public void setFrecuenciaCitas(Integer frecuenciaCitas) {
        this.frecuencia = frecuenciaCitas;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public Integer getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

}

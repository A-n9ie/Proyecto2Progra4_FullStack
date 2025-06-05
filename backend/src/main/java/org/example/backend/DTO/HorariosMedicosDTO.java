package org.example.backend.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class HorariosMedicosDTO {
    private Integer medicoId;
    private String diaSemana;
    private String horarioInicio;
    private String horarioFin;
    private Integer frecuencia;

    public HorariosMedicosDTO(Integer medicoId, String diaSemana, String horarioInicio, String horarioFin, Integer frecuencia) {
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

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public Integer getFrecuenciaCitas() {
        return frecuencia;
    }

    public void setFrecuenciaCitas(Integer frecuenciaCitas) {
        this.frecuencia = frecuenciaCitas;
    }

    public String getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(String horarioFin) {
        this.horarioFin = horarioFin;
    }

    public Integer getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

}

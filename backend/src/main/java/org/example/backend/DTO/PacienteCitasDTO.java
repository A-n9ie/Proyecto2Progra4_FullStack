package org.example.backend.DTO;
import org.example.backend.logic.Cita;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class PacienteCitasDTO {
    private String fotoUrlPaciente;
    private String nombrePaciente;
    //Medico
    private String nombreMedico;
    private String especialidad;
    private BigDecimal costoConsulta;
    private String lugarAtencion;
    private String fotoUrlMedico;
    // citas
    private Integer id;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String estado;
    private String anotaciones;

    public PacienteCitasDTO(Cita cita) {
        this.id = cita.getId();
        this.fechaCita = cita.getFechaCita();
        this.horaCita = cita.getHoraCita();
        this.estado = cita.getEstado();
        this.anotaciones = cita.getAnotaciones();

        if (cita.getPaciente() != null) {
            this.nombrePaciente = cita.getPaciente().getNombre();
            this.fotoUrlPaciente = cita.getPaciente().getFotoUrl();
        }

        if (cita.getMedico() != null) {
            this.nombreMedico = cita.getMedico().getNombre();
            this.especialidad = cita.getMedico().getEspecialidad();
            this.costoConsulta = cita.getMedico().getCostoConsulta();
            this.lugarAtencion = cita.getMedico().getLugarAtencion();
            this.fotoUrlMedico = cita.getMedico().getFotoUrl();
        }
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public BigDecimal getCostoConsulta() {
        return costoConsulta;
    }

    public void setCostoConsulta(BigDecimal costoConsulta) {
        this.costoConsulta = costoConsulta;
    }

    public String getLugarAtencion() {
        return lugarAtencion;
    }

    public void setLugarAtencion(String lugarAtencion) {
        this.lugarAtencion = lugarAtencion;
    }

    public String getFotoUrlPaciente() {
        return fotoUrlPaciente;
    }

    public void setFotoUrlPaciente(String fotoUrlPaciente) {
        this.fotoUrlPaciente = fotoUrlPaciente;
    }

    public String getFotoUrlMedico() {
        return fotoUrlMedico;
    }

    public void setFotoUrlMedico(String fotoUrlMedico) {
        this.fotoUrlMedico = fotoUrlMedico;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
    }

    public LocalTime getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(LocalTime horaCita) {
        this.horaCita = horaCita;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(String anotaciones) {
        this.anotaciones = anotaciones;
    }
}

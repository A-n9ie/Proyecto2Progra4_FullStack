package org.example.backend.DTO;

import org.example.backend.logic.HorariosMedico;
import org.example.backend.logic.Medico;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class PerfilMedicoDTO {
    private String cedula;
    private String nombre;
    private String usuario;
    //Medico
    private String especialidad;
    private BigDecimal costoConsulta;
    private String lugarAtencion;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private String frecuenciaCitas;
    private String fotoUrl;
    private String presentacion;

    List<HorariosMedico> dias;

    public PerfilMedicoDTO(Medico medico, List<HorariosMedico> horarios) {
        this.cedula = medico.getCedula();
        this.nombre = medico.getNombre();
        this.usuario = medico.getUsuario().getUsername();

        this.especialidad = medico.getEspecialidad();
        this.costoConsulta = medico.getCostoConsulta();
        this.lugarAtencion = medico.getLugarAtencion();
        this.horarioInicio = medico.getHorarioInicio();
        this.horarioFin = medico.getHorarioFin();
        this.frecuenciaCitas = medico.getFrecuenciaCitas();
        this.fotoUrl = medico.getFotoUrl();
        this.presentacion = medico.getPresentacion();

        dias = horarios;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public String getFrecuenciaCitas() {
        return frecuenciaCitas;
    }

    public void setFrecuenciaCitas(String frecuenciaCitas) {
        this.frecuenciaCitas = frecuenciaCitas;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }
}

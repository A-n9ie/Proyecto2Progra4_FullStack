package org.example.backend.DTO;

import org.example.backend.logic.Medico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class MedicosConHorariosDTO {
    private String nombre;
    private String especialidad;
    private BigDecimal costoConsulta;
    private String lugarAtencion;
    private String fotoUrl;
    private String presentacion;
    private Map<String, List<String>> horarios;

    public MedicosConHorariosDTO(Medico medico, Map<String, List<String>> horarios) {
        this.nombre = medico.getNombre();
        this.especialidad = medico.getEspecialidad();
        this.costoConsulta = medico.getCostoConsulta();
        this.lugarAtencion = medico.getLugarAtencion();
        this.fotoUrl = medico.getFotoUrl();
        this.presentacion = medico.getPresentacion();
        this.horarios = horarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getLugarAtencion() {
        return lugarAtencion;
    }

    public void setLugarAtencion(String lugarAtencion) {
        this.lugarAtencion = lugarAtencion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public Map<String, List<String>> getHorarios() {
        return horarios;
    }

    public void setHorarios(Map<String, List<String>> horarios) {
        this.horarios = horarios;
    }
}

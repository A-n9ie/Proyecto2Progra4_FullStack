package org.example.backend.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.backend.logic.HorariosMedico;
import org.example.backend.logic.Medico;
import org.example.backend.logic.Usuario;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class PerfilMedicoDTO {
    private int id;
    private String cedula;
    private String nombre;
    private String usuario;
    //Medico
    private String especialidad;
    private BigDecimal costoConsulta;
    private String lugarAtencion;
    private String fotoUrl;
    private String presentacion;

    @JsonIgnore
    private List<HorariosMedicosDTO> horarios;

    public PerfilMedicoDTO(Medico medico, List<HorariosMedicosDTO> horarios) {
        this.id = medico.getId();
        this.cedula = medico.getCedula();
        this.nombre = medico.getNombre();
        this.usuario = medico.getUsuario().getUsuario();
        this.especialidad = medico.getEspecialidad();
        this.costoConsulta = medico.getCostoConsulta();
        this.lugarAtencion = medico.getLugarAtencion();
        this.fotoUrl = medico.getFotoUrl();
        this.presentacion = medico.getPresentacion();
        this.horarios = horarios;
    }

    public PerfilMedicoDTO(Medico medico) {
        this.nombre = medico.getNombre();
        this.especialidad = medico.getEspecialidad();
        this.costoConsulta = medico.getCostoConsulta();
        this.lugarAtencion = medico.getLugarAtencion();
        this.fotoUrl = medico.getFotoUrl();
        this.presentacion = medico.getPresentacion();
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

    public void setUsuario(String  usuario) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<HorariosMedicosDTO> getDias() {
        return horarios;
    }

    public void setDias(List<HorariosMedicosDTO> horarios) {
        this.horarios = horarios;
    }
}

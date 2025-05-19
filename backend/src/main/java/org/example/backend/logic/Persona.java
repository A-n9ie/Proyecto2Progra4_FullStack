package org.example.backend.logic;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@MappedSuperclass
public class Persona {
    @Size(max = 30)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 30)
    @NotBlank(message = "Name is required")
    protected String nombre;

    @Size(max = 20)
    @NotNull
    @Column(name = "cedula", nullable = false, length = 20)
    @NotBlank(message = "ID is required and cannot be empty")
    protected String cedula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    protected Usuario usuario;

    public Persona() {
        this.nombre = "";
        this.cedula = "";
    }

    public Persona(Persona persona) {
        this.nombre = persona.getNombre();
        this.cedula = persona.getCedula();
        this.usuario = persona.getUsuario();
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {return cedula;}

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Usuario getUsuario() {return this.usuario;}

    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
}

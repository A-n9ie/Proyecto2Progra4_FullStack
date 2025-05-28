package org.example.backend.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "usuario", nullable = false, length = 20)
    @NotBlank(message = "User is required and cannot be empty")
    private String usuario;

    @Size(max = 255)
    @NotNull
    @Column(name = "clave", nullable = false)
    @NotBlank(message = "Password is required and cannot be empty")
    private String clave;

    @NotNull
    @Lob
    @Column(name = "rol", nullable = false)
    @NotBlank(message = "Rol is required")
    private String rol;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private Set<Medico> medicos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Paciente> pacientes = new LinkedHashSet<>();

    // Getters y setters originales
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Set<Medico> getMedicos() { return medicos; }
    public void setMedicos(Set<Medico> medicos) { this.medicos = medicos; }

    public Set<Paciente> getPacientes() { return pacientes; }
    public void setPacientes(Set<Paciente> pacientes) { this.pacientes = pacientes; }

    // Implementación de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.getRol()));

    }

    @Override
    public String getPassword() {
        return clave;
    }

    @Override
    public String getUsername() {
        return usuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

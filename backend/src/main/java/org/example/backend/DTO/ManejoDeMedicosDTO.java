package org.example.backend.DTO;

public class ManejoDeMedicosDTO {
    private Integer id;
    private String nombre;
    private String cedula;
    private String fotoPerfil;
    private Boolean  estado;
    public ManejoDeMedicosDTO(Integer id, String nombre, String cedula, String fotoPerfil, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.fotoPerfil = fotoPerfil;
        this.estado = estado;
    }

    // Getters y setters (puedes usar Lombok si lo tienes)
    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }
    public String getFotoPerfil() { return fotoPerfil; }
    public Boolean getEstado() { return estado; }

    public void setId(Integer id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}

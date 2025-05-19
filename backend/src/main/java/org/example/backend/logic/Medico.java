package org.example.backend.logic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "medicos")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @Size(max = 20)
    @NotNull
    @Column(name = "cedula", nullable = false, length = 20)
    @NotBlank(message = "ID is required and cannot be empty")
    private String cedula;

    @Size(max = 30)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 30)
    @NotBlank(message = "Name is required")
    private String nombre;

    @ColumnDefault("0")
    @Column(name = "aprobado")
    private Boolean aprobado;

    @Size(max = 30)
    @Column(name = "especialidad", length = 30)
    private String especialidad;

    @Column(name = "costo_consulta", precision = 10, scale = 2)
    private BigDecimal costoConsulta;

    @Size(max = 80)
    @Column(name = "lugar_atencion", length = 80)
    private String lugarAtencion;

    @Column(name = "horario_inicio")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horarioInicio;

    @Column(name = "horario_fin")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horarioFin;

    @Size(max = 20)
    @Column(name = "frecuencia_citas", length = 20)
    private String frecuenciaCitas;

    @Size(max = 255)
    @Column(name = "foto_url")
    private String fotoUrl;

    @Lob
    @Column(name = "presentacion")
    private String presentacion;

    @OneToMany(mappedBy = "medico")
    @JsonIgnore
    private Set<Cita> citas = new LinkedHashSet<>();

    //public Medico(Persona persona) {super(persona);}

    public Medico() {super();}

    public Medico(String nombre, String cedula, Usuario usuario){
        this.nombre = nombre;
        this.cedula = cedula;
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
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

    public Set<Cita> getCitas() {
        return citas;
    }

    public void setCitas(Set<Cita> citas) {
        this.citas = citas;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "id=" + id +
                '}';
    }

    /*
    public List<LocalTime> citasDisponibles() {
        List<LocalTime> horasDisponibles = new ArrayList<>(); //lista de horas
        LocalTime horaActual = horarioInicio;

        int frecuenciaEnMinutos = frecuenciaEnMinutos(this.frecuenciaCitas);

        while (!horaActual.isAfter(horarioFin)) {
            horasDisponibles.add(horaActual);
            horaActual = horaActual.plusMinutes(frecuenciaEnMinutos);
        }

        return horasDisponibles;
    }
     */

    public List<LocalTime> citasDisponibles(LocalDate fechaConsulta) {
        List<LocalTime> horasDisponibles = new ArrayList<>();
        LocalTime horaActual = horarioInicio;

        List<LocalDateTime> horarioOcupado = obtenerHorayFecha();
        int frecuenciaEnMinutos = frecuenciaEnMinutos(this.frecuenciaCitas);//convertir

        while (!horaActual.isAfter(horarioFin)) {
          //fecha de consulta
            LocalDateTime fechaHoraActual = LocalDateTime.of(fechaConsulta, horaActual);

            boolean ocupada = false;
            for (LocalDateTime fechaHoraOcupada : horarioOcupado) {

                if (fechaHoraOcupada.equals(fechaHoraActual)) {
                    ocupada = true; //Si la hora ya está ocupada en la fecha, la marcamos como ocupada
                    break;
                }
            }


            if (!ocupada) {
                horasDisponibles.add(horaActual);
            }
            horaActual = horaActual.plusMinutes(frecuenciaEnMinutos);
        }

        return horasDisponibles;
    }

    private int frecuenciaEnMinutos(String frecuencia) {
        if (frecuencia.contains("minutos")) {
            return Integer.parseInt(frecuencia.split(" ")[0]);
        } else if (frecuencia.contains("horas")) {
            return Integer.parseInt(frecuencia.split(" ")[0]) * 60;
        } else {
            return 30;
        }
    }

    public List<Cita> obtenerCitas(){
        List<Cita> citasMedico = new ArrayList<>();
        for(Cita cita : citas){
            if(cita.getMedico().equals(this)){
                citasMedico.add(cita);
            }
        }
        return citasMedico;
    }

//    public List<LocalTime> obtenerHora(){
//        List<Cita> citas = obtenerCitas();
//        List<LocalTime> horas = new ArrayList<>();
//         for(Cita cita : citas){
//             LocalTime hora = cita.getHoraCita();
//             horas.add(hora);
//         }
//         return horas;
//    }

    public List<LocalDateTime> obtenerHorayFecha(){
      List<Cita> citas = obtenerCitas();
        List<LocalDateTime> horasyFechas = new ArrayList<>();
         for(Cita cita : citas){
             LocalTime hora = cita.getHoraCita();
             LocalDate fecha = cita.getFechaCita();

             LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
             horasyFechas.add(fechaHora);
         }
         return horasyFechas;
    }



}
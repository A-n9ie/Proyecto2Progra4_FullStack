package org.example.backend.logic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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

//    @ColumnDefault("0")
    @Column(name = "aprobado")
    private Boolean aprobado = false;

    @Size(max = 30)
    @Column(name = "especialidad", length = 30)
    private String especialidad;

    @Column(name = "costo_consulta", precision = 10, scale = 2)
    private BigDecimal costoConsulta;

    @Size(max = 80)
    @Column(name = "lugar_atencion", length = 80)
    private String lugarAtencion;

//    @Column(name = "horario_inicio")
//    @DateTimeFormat(pattern = "HH:mm")
//    private LocalTime horarioInicio;
//
//    @Column(name = "horario_fin")
//    @DateTimeFormat(pattern = "HH:mm")
//    private LocalTime horarioFin;
//
//    @Size(max = 20)
//    @Column(name = "frecuencia_citas", length = 20)
//    private String frecuenciaCitas;

    @Size(max = 255)
    @Column(name = "foto_url")
    private String fotoUrl;

    @Lob
    @Column(name = "presentacion")
    private String presentacion;

    @OneToMany(mappedBy = "medico")
    private Set<HorariosMedico> horarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "medico")
    @JsonIgnore
    private Set<Cita> citas = new LinkedHashSet<>();

    //public Medico(Persona persona) {super(persona);}

    public Medico() {super();}

    public Medico(String nombre, String cedula, String fotoUrl, Usuario usuario){
        this.nombre = nombre;
        this.cedula = cedula;
        this.usuario = usuario;
        this.fotoUrl = fotoUrl;
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

//    public LocalTime getHorarioInicio() {
//        return horarioInicio;
//    }
//
//    public void setHorarioInicio(LocalTime horarioInicio) {
//        this.horarioInicio = horarioInicio;
//    }
//
//    public LocalTime getHorarioFin() {
//        return horarioFin;
//    }
//
//    public void setHorarioFin(LocalTime horarioFin) {
//        this.horarioFin = horarioFin;
//    }
//
//    public String getFrecuenciaCitas() {
//        return frecuenciaCitas;
//    }
//
//    public void setFrecuenciaCitas(String frecuenciaCitas) {
//        this.frecuenciaCitas = frecuenciaCitas;
//    }

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

    public Set<HorariosMedico> getHorarios() {
        return horarios;
    }

    public void setHorarios(Set<HorariosMedico> horarios) {
        this.horarios = horarios;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "id=" + id +
                '}';
    }

    public List<LocalTime> citasDisponibles(LocalDate fechaConsulta) {
        List<LocalTime> horasDisponibles = new ArrayList<>();

        DayOfWeek dia = fechaConsulta.getDayOfWeek();
        DiasSemana diaEnum = DiasSemana.valueOf(dia.name());
        Optional<HorariosMedico> horarioOpt = horarios.stream()
                .filter(h -> h.getDiaSemana().equals(diaEnum))
                .findFirst();

        if (horarioOpt.isEmpty()) {
            return horasDisponibles; //No trabaja ese dia
        }

        HorariosMedico horario = horarioOpt.get();
        LocalTime horaActual = horario.getHoraInicio();
        LocalTime horaFin = horario.getHoraFin();
        int frecuenciaEnMinutos = horario.getFrecuenciaCitas();

        List<LocalDateTime> horarioOcupado = obtenerHorayFecha();

        while (!horaActual.isAfter(horaFin)) {
            LocalDateTime fechaHoraActual = LocalDateTime.of(fechaConsulta, horaActual);

            boolean ocupada = horarioOcupado.contains(fechaHoraActual);

            if (!ocupada) {
                horasDisponibles.add(horaActual);
            }

            horaActual = horaActual.plusMinutes(frecuenciaEnMinutos);
        }

        return horasDisponibles;
    }

//    private int frecuenciaEnMinutos(String frecuencia) {
//        if (frecuencia.contains("minutos")) {
//            return Integer.parseInt(frecuencia.split(" ")[0]);
//        } else if (frecuencia.contains("horas")) {
//            return Integer.parseInt(frecuencia.split(" ")[0]) * 60;
//        } else {
//            return 30;
//        }
//    }

    public List<Cita> obtenerCitas(){
        List<Cita> citasMedico = new ArrayList<>();
        for(Cita cita : citas){
            if(cita.getMedico().equals(this)){
                citasMedico.add(cita);
            }
        }
        return citasMedico;
    }

    public List<LocalDateTime> obtenerHorayFecha(){
      List<Cita> citas = obtenerCitas();
        List<LocalDateTime> horasyFechas = new ArrayList<>();
         for(Cita cita : citas){
             horasyFechas.add(LocalDateTime.of(cita.getFechaCita(), cita.getHoraCita()));
         }
         return horasyFechas;
    }



}
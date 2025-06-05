package org.example.backend.logic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "horarios_medicos")
public class HorariosMedico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    @JsonIgnore
    private Medico medico;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia", nullable = false)
    private DiasSemana diadelaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "frecuencia_minutos")
    private Integer frecuenciaCitas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public DiasSemana getDiaSemana() {
        return diadelaSemana;
    }

    public void setDiaSemana(DiasSemana diaSemana) {
        this.diadelaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getFrecuenciaCitas() {
        return frecuenciaCitas;
    }

    public void setFrecuenciaCitas(Integer frecuenciaCitas) {
        this.frecuenciaCitas = frecuenciaCitas;
    }

    @Override
    public String toString() {
        return "HorariosMedico{" +
                "id=" + id +
                ", medico=" + medico +
                ", diaSemana=" + diadelaSemana +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", frecuenciaCitas=" + frecuenciaCitas +
                '}';
    }

    public String getFrecuenciaFormateada() {
        if (frecuenciaCitas % 60 == 0) {
            return (frecuenciaCitas / 60) + " hora(s)";
        } else {
            return frecuenciaCitas + " minutos";
        }
    }
}
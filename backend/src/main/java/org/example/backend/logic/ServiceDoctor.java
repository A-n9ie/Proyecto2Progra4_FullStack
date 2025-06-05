package org.example.backend.logic;

import jakarta.transaction.Transactional;
import org.example.backend.DTO.EditMedicoHorarioDTO;
import org.example.backend.DTO.HorariosMedicosDTO;
import org.example.backend.data.CitaRepository;
import org.example.backend.data.DoctorRepository;
import org.example.backend.data.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service("serviceDoctor")
public class ServiceDoctor {
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private CitaRepository citaRepository;

    public List<Medico> medicosFindAll() {
        return doctorRepository.findAll();
    }

    public Medico findDoctorById(Integer id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public Medico addDoctor(Medico doctor) {return doctorRepository.save(doctor);}

    public Medico findDoctor(String cedula) {return doctorRepository.findByCedula(cedula);}

    public Medico getDoctorbyUser(Usuario usuario) {return doctorRepository.findByUsuario(usuario);}

    public List<HorariosMedico> horarioMdico(Integer id){return horarioRepository.findByMedicoId(id);}

    public List<HorariosMedicosDTO> listarHorariosPorMedico(Medico medico) {
        List<HorariosMedico> entidades = horarioRepository.findAllByMedico(medico);

        return entidades.stream()
                .map(h -> new HorariosMedicosDTO(
                        h.getMedico().getId(),
                        h.getDiaSemana().name(),
                        h.getHoraInicio(),
                        h.getHoraFin(),
                        h.getFrecuenciaCitas()
                ))
                .collect(Collectors.toList());
    }

    public List<HorariosMedicosDTO> listarHorarios() {
        List<HorariosMedico> entidades = horarioRepository.findAll();

        return entidades.stream()
                .map(h -> new HorariosMedicosDTO(
                        h.getMedico().getId(),
                        h.getDiaSemana().name(),
                        h.getHoraInicio(),
                        h.getHoraFin(),
                        h.getFrecuenciaCitas()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void editHorariosMedico(EditMedicoHorarioDTO dto, Usuario user) {
        Medico existing = getDoctorbyUser(user);

        if (existing != null) {
            Medico edited = dto.getMedico();

            // Solo actualiza los campos que se pueden cambiar
            existing.setNombre(edited.getNombre());
            existing.setEspecialidad(edited.getEspecialidad());
            existing.setCostoConsulta(edited.getCostoConsulta());
            existing.setLugarAtencion(edited.getLugarAtencion());
            existing.setPresentacion(edited.getPresentacion());
            existing.setFotoUrl(edited.getFotoUrl());

            doctorRepository.save(existing);

            horarioRepository.deleteAllByMedico(existing);
            List<HorariosMedicosDTO> nuevosHorariosDto = dto.getHorarios();

            if (nuevosHorariosDto != null) {
                for (HorariosMedicosDTO horarioDto : nuevosHorariosDto) {
                    HorariosMedico horario = new HorariosMedico();
                    horario.setMedico(existing);
                    horario.setDiaSemana(DiasSemana.valueOf(horarioDto.getDiaSemana()));
                    horario.setHoraInicio(horarioDto.getHorarioInicio());
                    horario.setHoraFin(horarioDto.getHorarioFin());
                    horario.setFrecuenciaCitas(horarioDto.getFrecuenciaCitas());

                    horarioRepository.save(horario);
                }
        }
    }
    }


    public List<HorariosMedico> horariosMedicosFindAll() {
        return horarioRepository.findAll();
    }

    public Optional<HorariosMedico> obtenerHorarioPorMedico(Integer medicoId, String dia) { //puede no tener un horario un dia
        List<HorariosMedico> horarios = horarioRepository.findByMedicoId(medicoId);
        return horarios.stream()
                .filter(h -> h.getDiaSemana().equals(dia))
                .findFirst();
    }

    public Map<Integer, Map<String, List<String>>> listarHorariosAgrupados() {

        LocalDate hoy = LocalDate.now();
        LocalDate fechaFin = hoy.plusWeeks(2);

        List<HorariosMedicosDTO> lista = listarHorarios();

        Map<Integer, Map<String, List<String>>> result = new HashMap<>();

        LocalTime ahora = LocalTime.now();

        for (HorariosMedicosDTO dto : lista) {
            result.putIfAbsent(dto.getMedicoId(), new TreeMap<>());
            Map<String, List<String>> fechasMap = result.get(dto.getMedicoId());

            List<String> fechas = generarFechas(hoy, fechaFin, dto.getDiaSemana());
            List<String> horasDisponibles = calcularHoras(dto.getHorarioInicio(), dto.getHorarioFin(), dto.getFrecuenciaCitas());

            for (String fecha : fechas) {
                LocalDate fechaActual = LocalDate.parse(fecha);
                List<String> horasParaFecha;

                if (fechaActual.isEqual(hoy)) {
                    horasParaFecha = horasDisponibles.stream()
                            .filter(hora -> LocalTime.parse(hora).compareTo(ahora) >= 0)
                            .collect(Collectors.toList());

                    if (!horasParaFecha.isEmpty()) {
                        fechasMap.put(fecha, horasParaFecha);
                    }
                } else if (fechaActual.isAfter(hoy)) {
                    fechasMap.put(fecha, horasDisponibles);
                }
            }
        }

        return result;
    }

    private List<String> calcularHoras(LocalTime inicio, LocalTime fin, Integer frecuenciaMinutos) {
        List<String> horas = new ArrayList<>();
        LocalTime horaActual = inicio;

        while (!horaActual.isAfter(fin.minusMinutes(frecuenciaMinutos))) {
            horas.add(horaActual.toString());
            horaActual = horaActual.plusMinutes(frecuenciaMinutos);
        }

        return horas;
    }

    private List<String> generarFechas(LocalDate inicio, LocalDate fin, String diaSemana) {
        List<String> fechas = new ArrayList<>();

        Map<String, DayOfWeek> dias = Map.of(
                "LUNES", DayOfWeek.MONDAY,
                "MARTES", DayOfWeek.TUESDAY,
                "MIERCOLES", DayOfWeek.WEDNESDAY,
                "JUEVES", DayOfWeek.THURSDAY,
                "VIERNES", DayOfWeek.FRIDAY,
                "SABADO", DayOfWeek.SATURDAY,
                "DOMINGO", DayOfWeek.SUNDAY
        );
        DayOfWeek targetDay = dias.get(diaSemana.toUpperCase());

        LocalDate actual = inicio;
        while (!actual.isAfter(fin)) {
            if (actual.getDayOfWeek().equals(targetDay)) {
                fechas.add(actual.toString()); // formato YYYY-MM-DD
            }
            actual = actual.plusDays(1);
        }
        return fechas;
    }


    public Map<Integer, List<String>> obtenerHorariosDeMedicoEspecifico(Integer medicoId) {

        List<HorariosMedico> horarios = (List<HorariosMedico>) horariosMedicosFindAll();
        Map<Integer, List<String>> medicosConHorarios = new HashMap<>();

        LocalDate fechaBase = LocalDate.now();
        LocalDate fechaLimite = fechaBase.plusDays(14);
        // Map de días de la semana
        Map<String, Integer> diasDeLaSemana = new HashMap<>();
        diasDeLaSemana.put("Lunes", 1);
        diasDeLaSemana.put("Martes", 2);
        diasDeLaSemana.put("Miércoles", 3);
        diasDeLaSemana.put("Jueves", 4);
        diasDeLaSemana.put("Viernes", 5);
        diasDeLaSemana.put("Sábado", 6);
        diasDeLaSemana.put("Domingo", 7);

        for (LocalDate fecha = fechaBase; !fecha.isAfter(fechaLimite); fecha = fecha.plusDays(1)) {
            String nombreDia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());

            for (HorariosMedico horario : horarios) {
                if (horario.getDiaSemana().equals(nombreDia)) {
                    medicoId = horario.getMedico().getId();

                    // Agregar al mapa
                    medicosConHorarios.putIfAbsent(medicoId, new ArrayList<>());
                    List<String> diasDelMedico = medicosConHorarios.get(medicoId);

                    // Solo agregar si no está repetido
                    if (!diasDelMedico.contains(fecha.toString())) {
                        diasDelMedico.add(fecha.toString());
                    }
                }
            }
        }
                    List<String> fechasDelMedico = medicosConHorarios.get(medicoId);
                    if (fechasDelMedico != null && fechasDelMedico.size() > 3) {
                        List<String> fechasFiltradas = fechasDelMedico.stream()
                                .skip(3)
                                .collect(Collectors.toList());
                        medicosConHorarios.put(medicoId, fechasFiltradas);
        }
        return medicosConHorarios;
    }

    public Iterable<Medico> obtenerMedicosPorLugarYEspecialidad(String speciality, String city) {

        if (speciality.isEmpty() && city.isEmpty()) {
            return doctorRepository.findAll();
        }

        if (speciality.isEmpty()) {
            return doctorRepository.findByLugarAtencionContainingIgnoreCase(city);
        }

        if (city.isEmpty()) {
            return doctorRepository.findByEspecialidadContainingIgnoreCase(speciality);
        }
        return doctorRepository.findByEspecialidadContainingIgnoreCaseAndLugarAtencionContainingIgnoreCase(speciality, city);
    }

    public void cambiarEstado(int id, boolean aprobado) {
     Medico medico =  doctorRepository.findById(id);
     medico.setAprobado(aprobado);
     doctorRepository.save(medico);
    }

    public List<Medico> medicosAdmin(String doctor) {
        List<Medico> medicos = new ArrayList<>();
        for(Medico d: doctorRepository.findAll())
            if(d.getNombre().toLowerCase().equals(doctor.toLowerCase())
                    || d.getCedula().toLowerCase().equals(doctor.toLowerCase()))
                medicos.add(d);
        return medicos;
    }

}
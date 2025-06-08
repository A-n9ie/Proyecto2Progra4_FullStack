package org.example.backend.logic;

import jakarta.transaction.Transactional;
import org.example.backend.DTO.HorariosMedicosDTO;
import org.example.backend.DTO.PerfilMedicoDTO;
import org.example.backend.data.CitaRepository;
import org.example.backend.data.DoctorRepository;
import org.example.backend.data.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service("serviceDoctor")
public class ServiceDoctor {
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private ServiceAppointment serviceAppointment;

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

    public List<HorariosMedicosDTO> listarHorariosPorMedicoDTO(int medicoId) {
        List<HorariosMedico> entidades = horarioRepository.findByMedicoId(medicoId);

        return entidades.stream()
                .map(h -> new HorariosMedicosDTO(
                        h.getMedico().getId(),
                        h.getDiaSemana().name(),
                        h.getHoraInicio().toString(),
                        h.getHoraFin().toString(),
                        h.getFrecuenciaCitas()
                ))
                .collect(Collectors.toList());
    }

    public List<HorariosMedicosDTO> listarHorariosDTO() {
        List<HorariosMedico> entidades = horarioRepository.findAll();

        return entidades.stream()
                .map(h -> new HorariosMedicosDTO(
                        h.getMedico().getId(),
                        h.getDiaSemana().name(),
                        h.getHoraInicio().toString(),
                        h.getHoraFin().toString(),
                        h.getFrecuenciaCitas()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void editHorariosMedico(PerfilMedicoDTO dto, Usuario user) {
        Medico existing = getDoctorbyUser(user);

        if (existing != null) {
            dto.updateMedicoMedico(existing); // actualiza el existente con los datos del DTO
            doctorRepository.save(existing);

            horarioRepository.deleteAllByMedico(existing);
            List<HorariosMedicosDTO> nuevosHorariosDto = dto.getDias();

            if (nuevosHorariosDto != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                for (HorariosMedicosDTO horarioDto : nuevosHorariosDto) {
                    HorariosMedico horario = new HorariosMedico();
                    horario.setMedico(existing);
                    horario.setDiaSemana(DiasSemana.valueOf(horarioDto.getDiaSemana()));
                    LocalTime inicio = LocalTime.parse(horarioDto.getHorarioInicio(), formatter);
                    LocalTime fin = LocalTime.parse(horarioDto.getHorarioFin(), formatter);
                    horario.setHoraInicio(inicio);
                    horario.setHoraFin(fin);
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

        //desde hoy
        LocalDate hoy = LocalDate.now();
        //dentro de dos semanas
        LocalDate fechaFin = hoy.plusWeeks(2);
        LocalTime ahora = LocalTime.now();

        List<HorariosMedicosDTO> lista = listarHorariosDTO();

        Map<Integer, Map<String, Set<String>>> citasMap = serviceAppointment.obtenerCitasMap(hoy, fechaFin);
        Map<Integer, Map<String, List<String>>> result = new HashMap<>();


        for (HorariosMedicosDTO dto : lista) {
            result.putIfAbsent(dto.getMedicoId(), new TreeMap<>());
            Map<String, List<String>> fechasMap = result.get(dto.getMedicoId());

            List<String> fechas = generarFechas(hoy, fechaFin, dto.getDiaSemana());
            List<String> horasGeneradas = calcularHoras(dto.getHorarioInicio(), dto.getHorarioFin(), dto.getFrecuenciaCitas());

            for (String fecha : fechas) {
                LocalDate fechaActual = LocalDate.parse(fecha);

                //id medico(fecha y hora)
                Set<String> horasOcupadas = citasMap
                        .getOrDefault(dto.getMedicoId(), Collections.emptyMap())
                        .getOrDefault(fecha, Collections.emptySet());

                List<String> horasFiltradas = horasGeneradas.stream()
                        .filter(h -> !horasOcupadas.contains(h))
                        .collect(Collectors.toList());

                if (fechaActual.isEqual(hoy)) {
                    List<String> horasHoy = horasFiltradas.stream()
                            .filter(h -> LocalTime.parse(h).compareTo(ahora) >= 0)
                            .collect(Collectors.toList());

                    if (!horasHoy.isEmpty()) {
                        fechasMap.put(fecha, horasHoy);
                    }
                } else if (fechaActual.isAfter(hoy) && !horasFiltradas.isEmpty()) {
                    fechasMap.put(fecha, horasFiltradas);
                }
            }
        }

        return result;
    }

    private List<String> calcularHoras(String horaInicio, String horaFin, Integer frecuenciaMinutos) {
        List<String> horas = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime inicio = LocalTime.parse(horaInicio, formatter);
        LocalTime fin = LocalTime.parse(horaFin, formatter);

        while (!inicio.isAfter(fin.minusMinutes(frecuenciaMinutos))) {
            horas.add(inicio.format(formatter));
            inicio = inicio.plusMinutes(frecuenciaMinutos);
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

        while (!inicio.isAfter(fin)) {
            if (inicio.getDayOfWeek().equals(targetDay)) {
                fechas.add(inicio.toString());
            }
            inicio = inicio.plusDays(1);
        }
        return fechas;
    }


    public Map<String, List<String>> listarHorariosAgrupadosPorMedico(int medicoId) {

        //desde hoy
        LocalDate hoy = LocalDate.now();
        //dentro de dos semanas
        LocalDate fechaFin = hoy.plusWeeks(2);
        LocalTime ahora = LocalTime.now();


        Medico medico = doctorRepository.findById(medicoId);
        if (medico == null) {
            throw new IllegalArgumentException("Médico no encontrado con ID: " + medicoId);
        }

        List<HorariosMedicosDTO> lista = listarHorariosPorMedicoDTO(medicoId);

        Map<Integer, Map<String, Set<String>>> citasMap = serviceAppointment.obtenerCitasMap(hoy, fechaFin);
        Map<String, List<String>> result = new HashMap<>();

            for(HorariosMedicosDTO dto: lista){//recorre la lista de horarios de ese medico
            List<String> fechas = generarFechas(hoy, fechaFin, dto.getDiaSemana());
            List<String> horasGeneradas = calcularHoras(dto.getHorarioInicio(), dto.getHorarioFin(), dto.getFrecuenciaCitas());

            for (String fecha : fechas) {
                LocalDate fechaActual = LocalDate.parse(fecha);

                //id medico(fecha y hora)
                Set<String> horasOcupadas = citasMap
                        .getOrDefault(dto.getMedicoId(), Collections.emptyMap())
                        .getOrDefault(fecha, Collections.emptySet());

                List<String> horasFiltradas = horasGeneradas.stream()
                        .filter(h -> !horasOcupadas.contains(h))
                        .collect(Collectors.toList());

                if (fechaActual.isEqual(hoy)) {
                    List<String> horasHoy = horasFiltradas.stream()
                            .filter(h -> LocalTime.parse(h).compareTo(ahora) >= 0)
                            .collect(Collectors.toList());

                    if (!horasHoy.isEmpty()) {
                        result.put(fecha, horasHoy);
                    }
                } else if (fechaActual.isAfter(hoy) && !horasFiltradas.isEmpty()) {
                    result.put(fecha, horasFiltradas);
                }
            }
        }

        return result;
    }

    public List<Medico> obtenerMedicosPorLugarYEspecialidad(String speciality, String city) {

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

    public List<Medico> findMedicosPendientes() {
        return doctorRepository.findByAprobadoFalse();
    }

}
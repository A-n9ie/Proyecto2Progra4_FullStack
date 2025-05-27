package org.example.backend.logic;

import jakarta.transaction.Transactional;
import org.example.backend.data.CitaRepository;
import org.example.backend.data.DoctorRepository;
import org.example.backend.data.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
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


    public void editDoctor(Usuario user, Medico doctor){
        Medico u = getDoctorbyUser(user);
        if (u != null){
            u.setNombre(doctor.getNombre());
            u.setEspecialidad(doctor.getEspecialidad());
            u.setCostoConsulta(doctor.getCostoConsulta());
            u.setFrecuenciaCitas(doctor.getFrecuenciaCitas());
            u.setHorarioInicio(doctor.getHorarioInicio());
            u.setHorarioFin(doctor.getHorarioFin());
            u.setLugarAtencion(doctor.getLugarAtencion());
            u.setPresentacion(doctor.getPresentacion());

            doctorRepository.save(u);
        }
    }

    @Transactional
    public void editDays(Medico doctor, List<String> dias){
        Medico u = findDoctor(doctor.getCedula());
        horarioRepository.deleteAllByMedico(u);
        for(String d: dias) {
            HorariosMedico horario = new HorariosMedico();
            horario.setMedico(u);
            horario.setDia(d);
            horarioRepository.save(horario);
        }
    }

    public Iterable<HorariosMedico> horariosMedicosFindAll() {
        return horarioRepository.findAll();
    }

    public Map<Integer, List<String>> obtenerMedicosConHorariosFechas() {
        List<HorariosMedico> horarios = (List<HorariosMedico>) horariosMedicosFindAll();
        Map<Integer, List<String>> medicosConHorarios = new HashMap<>();

       //hoy
        LocalDate fechaBase = LocalDate.now();
        LocalDate fechaLimite = fechaBase.plusDays(14);
        //dia, id
        Map<String, Integer> diasDeLaSemana = new HashMap<>();
        diasDeLaSemana.put("Lunes", 1);
        diasDeLaSemana.put("Martes", 2);
        diasDeLaSemana.put("Miércoles", 3);
        diasDeLaSemana.put("Jueves", 4);
        diasDeLaSemana.put("Viernes", 5);
        diasDeLaSemana.put("Sábado", 6);
        diasDeLaSemana.put("Domingo", 7);

        for (LocalDate fecha = fechaBase; !fecha.isAfter(fechaLimite); fecha = fecha.plusDays(1)) {
            String nombreDia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"));

            for (HorariosMedico horario : horarios) {
                if (horario.getDia().toLowerCase().equals(nombreDia.toLowerCase())) {
                    Integer medicoId = horario.getMedico().getId();

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
        return medicosConHorarios;
    }

    /// ////////// PRUEBBBBBAAAAA///////////////
    public Map<Integer, Map<String, List<String>>> obtenerMedicosConFechasYHoras() {
        Map<Integer, List<String>> fechasPorMedico = obtenerMedicosConHorariosFechas(); // tu método actual
        Map<Integer, Map<String, List<String>>> resultado = new HashMap<>();

        for (Map.Entry<Integer, List<String>> entry : fechasPorMedico.entrySet()) {
            Integer medicoId = entry.getKey();
            List<String> fechas = entry.getValue();

            Medico medico = doctorRepository.findById(medicoId).orElse(null); // o como accedas al Medico
            if (medico == null) continue;

            Map<String, List<String>> fechasConHoras = new LinkedHashMap<>();

            for (String fechaStr : fechas) {
                LocalDate fecha = LocalDate.parse(fechaStr);
                List<LocalTime> horas = medico.citasDisponibles(fecha);

                if (!horas.isEmpty()) {
                    List<String> horasFormateadas = horas.stream()
                            .map(LocalTime::toString)
                            .collect(Collectors.toList());

                    fechasConHoras.put(fechaStr, horasFormateadas);
                }
            }

            if (!fechasConHoras.isEmpty()) {
                resultado.put(medicoId, fechasConHoras);
            }
        }

        return resultado;
    }

    private LocalDate calcularFechaParaDiaSemana(LocalDate hoy, int diaSemana) {
        //hoy
        LocalDate fecha = hoy;

        int diasHastaDia = diaSemana - fecha.getDayOfWeek().getValue();
        if (diasHastaDia < 0) {
            diasHastaDia += 7;
        }

        return fecha.plusDays(diasHastaDia);
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

//        for (HorariosMedico horario : horarios) {
//            //sacr el id del medico
//            medicoId = horario.getMedico().getId();
//            String dia = horario.getDia();
//
//            LocalDate fechaDia = calcularFechaParaDiaSemana(fechaBase, diasDeLaSemana.get(dia));
//
//            // Agregar la fecha al mapa
//            medicosConHorarios.putIfAbsent(medicoId, new ArrayList<>());
//            List<String> diasDelMedico = medicosConHorarios.get(medicoId);
//
//            if (!diasDelMedico.contains(fechaDia.toString())) {
//                diasDelMedico.add(fechaDia.toString());
//            }
//        }
        for (LocalDate fecha = fechaBase; !fecha.isAfter(fechaLimite); fecha = fecha.plusDays(1)) {
            String nombreDia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());

            for (HorariosMedico horario : horarios) {
                if (horario.getDia().equalsIgnoreCase(nombreDia)) {
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
package org.example.backend.presentation.patient;

import org.example.backend.DTO.PacienteCitasDTO;
import org.example.backend.DTO.PerfilMedicoDTO;
import org.example.backend.data.UsuarioRepository;
import org.example.backend.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes")
public class ControllerPatient {
    @Autowired
    private ServicePatient servicePatient;
    @Autowired
    private ServiceAppointment serviceAppointment;
    @Autowired
    private ServiceDoctor serviceDoctor;
    @Autowired
    private ServiceUser serviceUser;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    public Paciente profile(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);
        Paciente paciente = servicePatient.getPatientByUser(usuario);
        paciente.setFotoUrl(serviceUser.cargarFoto(paciente.getFotoUrl()));
        return paciente;
    }

    @PutMapping("/update")
    public Paciente edit(@RequestBody Paciente paciente, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);

        servicePatient.editPatient(usuario, paciente);
        return servicePatient.findPatient(paciente.getCedula());
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);
        Paciente paciente = servicePatient.getPatientByUser(usuario);

        List<Cita> citas = serviceAppointment.citasPaciente(paciente);

        List<PacienteCitasDTO> citaDTOs = citas.stream()
                .map(c -> {
                    PacienteCitasDTO dto = new PacienteCitasDTO(c);
                    dto.setFotoUrlMedico(serviceUser.cargarFoto(dto.getFotoUrlMedico()));
                    return dto;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("paciente", paciente.getNombre());
        response.put("citas", citaDTOs);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/filter")
    public ResponseEntity<?> filterHistory(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String doctor
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);
        Paciente paciente = servicePatient.getPatientByUser(usuario);

        List<Cita> citas = serviceAppointment.citasPacienteFiltradas(paciente, status, doctor);

        List<PacienteCitasDTO> citaDTOs = citas.stream()
                .map(c -> {
                    PacienteCitasDTO dto = new PacienteCitasDTO(c);
                    dto.setFotoUrlMedico(serviceUser.cargarFoto(dto.getFotoUrlMedico()));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(citaDTOs);
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "speciality", required = false) String speciality,
                         @RequestParam(value = "city", required = false) String city, Model model) {

        Iterable<Medico> doctorByLocation = serviceDoctor.obtenerMedicosPorLugarYEspecialidad(speciality, city);
        model.addAttribute("medicos", doctorByLocation);
        return "/presentation/principal/index";
    }


    @PostMapping("/presentation/patient/book/save")
    public String saveAppointment(@RequestParam("dia") String fecha_cita,
                                  @RequestParam("hora") String hora_cita,
                                  @RequestParam Integer medicoId,
                                  Model model) {
        try {
            // Obtener usuario autenticado
            String username = serviceUser.getUserAuthenticated();
            Usuario usuario = serviceUser.getUser(username);
            // Convertir fecha y hora
            LocalDate fecha = LocalDate.parse(fecha_cita, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime hora = LocalTime.parse(hora_cita, DateTimeFormatter.ofPattern("HH:mm"));
            System.out.println("Fecha y hora recibidas: " + fecha + " " + hora);

            // Buscar el médico
            Medico medico = serviceDoctor.findDoctorById(medicoId);
            if (medico == null) {
                model.addAttribute("error", "Médico no encontrado.");
                return "/presentation/principal/index";
            }
            // Obtener pacientes del usuario
            Set<Paciente> pacientes = usuario.getPacientes();

            if (pacientes == null || pacientes.isEmpty()) {
                model.addAttribute("error", "No hay pacientes asociados al usuario.");
                return "/presentation/principal/index";
            }
            System.out.println("Pacientes encontrados: " + pacientes.size());

            Paciente paciente = pacientes.iterator().next();

            model.addAttribute("fecha", fecha);
            model.addAttribute("hora", hora);
            model.addAttribute("medico", medico);
            model.addAttribute("paciente", paciente);

            return "/presentation/patient/book";

        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Formato de fecha u hora incorrecto.");
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al guardar la cita.");
        }
        return "redirect:/presentation/patient/book";
    }

    @PostMapping("/confirmar")
    public String confirmarCita(@RequestParam(value = "si", required = false) String confirmar,
//                                @RequestParam(value = "no", required = false) String rechazar,
//                                @RequestHeader ("usuario") String usuario,
                                @RequestParam("dia") String fecha_cita,
                                @RequestParam("hora") String hora_cita,
                                @RequestParam("medicoId") Integer medicoId,
                                Model model) {
        System.out.println("Confirmar cita llamada con:");
        System.out.println("confirmar: " + confirmar);
        System.out.println("fecha: " + fecha_cita);
        System.out.println("hora: " + hora_cita);
        System.out.println("medicoId: " + medicoId);
//        if (rechazar != null) {
//            return "redirect:";
//        }

        if(confirmar != null) {
            try {

                Usuario usuarioAutenticado = serviceUser.getUser("Glucas");

                // Convertir fecha y hora
                LocalDate fecha = LocalDate.parse(fecha_cita, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalTime hora = LocalTime.parse(hora_cita, DateTimeFormatter.ofPattern("HH:mm"));

                // Buscar el médico
                Medico medico = serviceDoctor.findDoctorById(medicoId);
                if (medico == null) {
                    model.addAttribute("error", "Médico no encontrado.");
                    return "redirect:";
                }

                // Obtener pacientes del usuario
                Set<Paciente> pacientes = usuarioAutenticado.getPacientes();
                if (pacientes == null || pacientes.isEmpty()) {
                    model.addAttribute("error", "No hay pacientes asociados al usuario.");
                    return "redirect:";
                }

                Paciente paciente = pacientes.iterator().next();

                // Crear la cita
                Cita nuevaCita = new Cita();
                nuevaCita.setFechaCita(fecha);
                nuevaCita.setHoraCita(hora);
                nuevaCita.setMedico(medico);
                nuevaCita.setPaciente(paciente);
                nuevaCita.setEstado("Pendiente");

                // Guardar la cita
                serviceAppointment.saveAppointment(nuevaCita);

                return "redirect:/presentation/patient/history/show";

            } catch (DateTimeParseException e) {
                model.addAttribute("error", "Formato de fecha u hora incorrecto.");
            } catch (Exception e) {
                model.addAttribute("error", "Ocurrió un error al confirmar la cita.");
            }
        }

        return "redirect:";
    }

}

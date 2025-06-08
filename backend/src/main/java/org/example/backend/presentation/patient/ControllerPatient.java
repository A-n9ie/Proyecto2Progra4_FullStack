package org.example.backend.presentation.patient;

import org.example.backend.DTO.MedicosConHorariosDTO;
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

    @GetMapping("/buscar")
    public List<MedicosConHorariosDTO> search(@RequestParam(required = false) String speciality,
                         @RequestParam(required = false) String city) {

        List<Medico> medicosFiltrados = serviceDoctor.obtenerMedicosPorLugarYEspecialidad(speciality, city);
        Map<Integer, Map<String, List<String>>> horariosAgrupados = serviceDoctor.listarHorariosAgrupados();

        return medicosFiltrados.stream()
                .map(medico -> new MedicosConHorariosDTO(
                        medico,
                        horariosAgrupados.getOrDefault(medico.getId(), Map.of())
                ))
                .collect(Collectors.toList());

    }

    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarCita(@RequestParam(value = "si", required = false) String confirmar,
                                @RequestParam("dia") String fecha_cita,
                                @RequestParam("hora") String hora_cita,
                                @RequestParam("idMedico") int medicoId,
                                Model model,
                                Authentication authentication) {
        if(confirmar != null) {
            try {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                String nombre = jwt.getClaim("name");
                Usuario usuario = serviceUser.getUser(nombre);

                LocalDate fecha = LocalDate.parse(fecha_cita, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalTime hora = LocalTime.parse(hora_cita, DateTimeFormatter.ofPattern("HH:mm"));

                Medico medico = serviceDoctor.findDoctorById(medicoId);
                if (medico == null) {
                    model.addAttribute("error", "Médico no encontrado.");
                    return ResponseEntity.badRequest().body("Médico no encontrado.");
                }

                Set<Paciente> pacientes = usuario.getPacientes();
                if (pacientes == null || pacientes.isEmpty()) {
                    model.addAttribute("error", "No hay pacientes asociados al usuario.");
                    return ResponseEntity.badRequest().body("No hay pacientes asociados al usuario.");
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

                return ResponseEntity.ok("Cita confirmada exitosamente.");
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Formato de fecha u hora incorrecto.");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Ocurrió un error al confirmar la cita.");
            }
        }

        return ResponseEntity.badRequest().body("Confirmación inválida.");
    }

}

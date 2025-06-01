package org.example.backend.presentation.doctor;

import org.example.backend.DTO.PacienteCitasDTO;
import org.example.backend.DTO.PerfilMedicoDTO;
import org.example.backend.data.HorarioRepository;
import org.example.backend.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medicos")
public class ControllerDoctor {
    @Autowired
    private ServiceDoctor serviceDoctor;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private ServiceAppointment serviceAppointment;
    @Autowired
    private ServiceUser serviceUser;


    @GetMapping
    public List<Medico> getMedicos() {
        return serviceDoctor.medicosFindAll();
    }


    @GetMapping("/horarios")
    public Map<Integer, Map<String, List<String>>> getMedicosConHorarios() {
        return serviceDoctor.obtenerMedicosConFechasYHoras();
    }

    @PostMapping("/save")
    public Medico save(@RequestBody Medico medico) {
        if(serviceDoctor.findDoctor(medico.getCedula()) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor Already Exist");
        }
        serviceUser.addUser(medico.getUsuario());
        return serviceDoctor.addDoctor(medico);
    }

    @GetMapping("/me")
    public PerfilMedicoDTO profile(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);
        Medico medico = serviceDoctor.getDoctorbyUser(usuario);
        List<HorariosMedico> list = serviceDoctor.horarioMdico(medico.getId());
        return new PerfilMedicoDTO(medico, list);
    }

    @PutMapping("/update")
    public Medico edit(@RequestBody Medico medico, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);

        serviceDoctor.editDoctor(usuario, medico);
        return serviceDoctor.findDoctor(medico.getCedula());
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);
        Medico medico = serviceDoctor.getDoctorbyUser(usuario);

        List<Cita> citas = serviceAppointment.citasMedico(medico);

        List<PacienteCitasDTO> citaDTOs = citas.stream()
                .map(PacienteCitasDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(citaDTOs);
    }

    @PutMapping("/cancel")
    public void cancel(Authentication authentication, @RequestBody Map<String, Integer> body) {
        Integer citaId = body.get("citaId");
        Cita citaSeleccionada = serviceAppointment.getCitaById(citaId);
        if (citaSeleccionada != null) {
            serviceAppointment.deleteAppointment(citaSeleccionada);
        }
    }

    @PutMapping("/saveNote")
    public void saveNote(Authentication authentication, @RequestBody Map<String, Object> datos) {
        Integer citaId = (Integer) datos.get("citaId");
        String anotaciones = (String) datos.get("notas");
        Cita cita = serviceAppointment.getCitaById(citaId);
        if (cita != null) {
            cita.setAnotaciones(anotaciones);
            cita.setEstado("Atendida");
            serviceAppointment.saveAppointment(cita);
        }
    }

    @GetMapping("/patient/schedule/{id}")
    public String showSchedule(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page, Model model) {
        Medico medico = serviceDoctor.findDoctorById(id);
        if (medico == null) {
            return "redirect:/error";
        }

        int pageSize = 3;
        Map<Integer, List<String>> horarios = serviceDoctor.obtenerHorariosDeMedicoEspecifico(id);

        List<String> fechas = horarios.get(medico.getId());
        if (fechas == null) {
            return "redirect:/error";
        }

        int totalDias = fechas.size();
        int totalPages = (int) Math.ceil((double) totalDias / pageSize);


        model.addAttribute("medico", medico);
        model.addAttribute("medicoHorarios", fechas);
        model.addAttribute("page", page);
        model.addAttribute("totalDias", totalDias);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("medicosHorarios", horarios);

        return "/presentation/patient/schedule";
    }

    @GetMapping("/administrador/management")
    public String showDocsForApproval(Model model) {
        model.addAttribute("Doctors", serviceDoctor.medicosFindAll());
        return "/presentation/administrator/management";
    }

    @PostMapping("/aprobar")
    public String aprobarDoctor(@RequestParam("id") int id, Model model) {
        serviceDoctor.cambiarEstado(id, true);
        model.addAttribute("Doctors", serviceDoctor.medicosFindAll());
        return "/presentation/administrator/management";
    }


}
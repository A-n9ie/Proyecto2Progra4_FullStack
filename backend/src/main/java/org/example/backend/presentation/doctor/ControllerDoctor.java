package org.example.backend.presentation.doctor;

import org.example.backend.DTO.PerfilMedicoDTO;
import org.example.backend.data.HorarioRepository;
import org.example.backend.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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

    @GetMapping("/profile/{name}")
    public PerfilMedicoDTO profile(@PathVariable String name) {
        Medico medico = serviceDoctor.getDoctorbyUser(serviceUser.getUser(name));
        return new PerfilMedicoDTO(medico);
    }

    @GetMapping("/profile/days/{cedula}")
    public List<HorariosMedico> profileDays(@PathVariable String cedula) {
        List<HorariosMedico> list = serviceDoctor.horarioMdico(serviceDoctor.findDoctor(cedula).getId());
        if(list.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista no encontrada");
        return list;
    }

    @PutMapping("/profile/update/{name}")
    public Medico edit(@PathVariable String name, @RequestBody Medico medico) {
        if(serviceDoctor.findDoctor(medico.getCedula()) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        serviceDoctor.editDoctor(serviceUser.getUser(name),medico);
        return serviceDoctor.findDoctor(medico.getCedula());
    }

    @PutMapping("/profile/saveDays/{cedula}")
    public List<HorariosMedico> updateDias(@PathVariable String cedula, @RequestBody List<String> dias) {
        Medico medico = serviceDoctor.findDoctor(cedula);
        serviceDoctor.editDays(medico, dias);
        return serviceDoctor.horarioMdico(medico.getId());
    }

    @GetMapping("/appointment/show")
    public String historyShow(@ModelAttribute("usuario") Usuario user,
                              Model model) {
        Medico medico = serviceDoctor.getDoctorbyUser(user);

        List<Cita> citas = serviceAppointment.citasMedico(medico);
        model.addAttribute("citas", citas);
        model.addAttribute("nombre", medico.getNombre());

        return "/presentation/doctor/appointment";
    }

    @GetMapping("/history/filter")
    public String historyEstado(
            @RequestParam(value = "status", required = false, defaultValue = "All") String status,
            @RequestParam(value = "patient", required = false, defaultValue = "") String paciente,
            @RequestParam(value = "show", required = false) Integer show,
            @RequestParam(value = "approve", required = false) Integer approve,
            @RequestParam(value = "cancel", required = false) Integer cancel,
            Model model) {

        String username = serviceUser.getUserAuthenticated();
        Usuario user = serviceUser.getUser(username);
        Medico medico = serviceDoctor.getDoctorbyUser(user);

        if (show != null) {
            Cita citaSeleccionada = serviceAppointment.getCitaById(show);
            model.addAttribute("citaSeleccionada", citaSeleccionada);
        }
        if (approve != null) {
            Cita citaSeleccionada = serviceAppointment.getCitaById(approve);
            if (citaSeleccionada != null) {
                serviceAppointment.saveAppointment(citaSeleccionada);
                model.addAttribute("citaAprovada", citaSeleccionada);
            }
        }
        if (cancel != null) {
            Cita citaSeleccionada = serviceAppointment.getCitaById(cancel);
            if (citaSeleccionada != null) {
                serviceAppointment.deleteAppointment(citaSeleccionada);
            }
        }

        List<Cita> citasFiltradas = serviceAppointment.citasMedicoFiltradas(medico, status, paciente);
        if(status.equals("All") && paciente.isEmpty()) {
            citasFiltradas = serviceAppointment.citasMedico(medico);
        }
        model.addAttribute("citas", citasFiltradas);
        model.addAttribute("nombre", medico.getNombre());


        return "/presentation/doctor/appointment";
    }

    @PostMapping("/history/saveNote")
    public String saveNote(
            @RequestParam("citaId") Integer citaId,
            @RequestParam("anotaciones") String anotaciones) {

        Cita cita = serviceAppointment.getCitaById(citaId);
        if (cita != null) {
            cita.setAnotaciones(anotaciones);
            cita.setEstado("Atendida");
            serviceAppointment.saveAppointment(cita);
        }

        return "redirect:/presentation/doctor/history/filter";
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
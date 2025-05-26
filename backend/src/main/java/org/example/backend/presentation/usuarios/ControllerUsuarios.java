package org.example.backend.presentation.usuarios;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.*;
import org.example.backend.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Controller("usuarios")

public class ControllerUsuarios {
    @Autowired
    private ServiceUser serviceUser;
    @Autowired
    private ServiceDoctor serviceDoctor;
    @Autowired
    private ServicePatient servicePatient;

    @PostMapping("/notAuthorized")
    public String error(Model model) {
        model.addAttribute("error", "Acceso NO autorizado");
        return "/presentation/error";
    }

    @GetMapping("/presentation/usuarios/show")
    public String show(Model model) {
        model.addAttribute("usuarios", serviceUser.usuariosFindAll());
        return "/presentation/usuarios/register";
    }

    //Modelo vacio para colocar al inicio de la pagina
    @GetMapping("/presentation/usuarios/registerSys")
    public String register(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("persona", new Persona());
        return "/presentation/usuarios/register";
    }



    @PostMapping("/presentation/usuarios/create")
    public String create(@Valid @ModelAttribute Usuario usuario,
                         @Valid @ModelAttribute Persona persona,
                         BindingResult result,
                         @RequestParam("password_c") String passwordConfirm,
                         @RequestParam("photo") MultipartFile photo,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("error", "Error en los datos de entrada");
            return "/presentation/usuarios/register";
        }

        try {
            if ("Medico".equals(usuario.getRol()) && serviceDoctor.findDoctor(persona.getCedula()) != null) {
                throw new IllegalArgumentException("Doctor ya existe");
            }

            if ("Paciente".equals(usuario.getRol()) && servicePatient.findPatient(persona.getCedula()) != null) {
                throw new IllegalArgumentException("Paciente ya existe");
            }

            serviceUser.addUser(usuario);
            usuario = serviceUser.getLastUser();
            persona.setUsuario(usuario);

            // **Guardar en una carpeta dentro del proyecto llamada "uploads/fotosPerfil"**
            String directoryPath = new File("uploads/fotosPerfil").getAbsolutePath();

            // Crear la carpeta si no existe
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();  // Crear directorio si no existe
            }

            // Generar un nombre único para evitar sobrescribir archivos
            String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
            String photoPath = directoryPath + "/" + fileName;

            // Guardar la foto en la carpeta
            photo.transferTo(new File(photoPath));

            // Guardamos solo el nombre del archivo en la base de datos
            if ("Medico".equals(usuario.getRol())) {
                Medico doctor = new Medico(persona.getNombre(), persona.getCedula(), persona.getUsuario());
                doctor.setAprobado(false);
                doctor.setFotoUrl(fileName);
                serviceDoctor.addDoctor(doctor);
            } else {
                Paciente patient = new Paciente(persona.getNombre(), persona.getCedula(), persona.getUsuario());
                patient.setFotoUrl(fileName);
                servicePatient.addPatient(patient);
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/presentation/usuarios/register";
        }

        return "/presentation/usuarios/login";
    }






    @GetMapping("/presentation/perfil/show")
    public String profile(RedirectAttributes redirect) {
        String username = serviceUser.getUserAuthenticated();
        Usuario usuario = serviceUser.getUser(username);
        if (usuario.getRol().equals("Medico")) {
            redirect.addFlashAttribute("usuario", usuario);
            return "redirect:/presentation/doctor/profile";
        } else {
            redirect.addFlashAttribute("usuario", usuario);
            return "redirect:/presentation/patient/profile";
        }
    }

    @PostMapping("/presentation/perfil/edit")
    public String edit(RedirectAttributes redirect,
                       @ModelAttribute("medico") Medico medico,
                       @ModelAttribute("paciente") Paciente paciente,
                       @RequestParam(value = "days", required = false) List<String> selectedDays,
                       @RequestParam(value = "numero", required = false) Integer numero,
                       @RequestParam(value = "frecuencia", required = false) String frecuencia) {
        String username = serviceUser.getUserAuthenticated();
        Usuario usuario = serviceUser.getUser(username);
        redirect.addFlashAttribute("usuario", usuario);
        if (usuario.getRol().equals("Medico")) {
            redirect.addFlashAttribute("medico", medico);
            redirect.addFlashAttribute("numero", numero);
            redirect.addFlashAttribute("frecuencia", frecuencia);
            redirect.addFlashAttribute("days", selectedDays);
            return "redirect:/presentation/doctor/edit";
        } else {
            redirect.addFlashAttribute("paciente", paciente);
            return "redirect:/presentation/patient/edit";
        }
    }

    @GetMapping("/presentation/usuarios/history")
    public String historyShow(
            @RequestParam(value = "show", required = false) Long showId,
            RedirectAttributes redirect) {
        String username = serviceUser.getUserAuthenticated();
        Usuario usuario = serviceUser.getUser(username);
        redirect.addFlashAttribute("usuario", usuario);
        if(usuario.getRol().equals("Paciente"))
            return "redirect:/presentation/patient/history/show";
        else
            return "redirect:/presentation/doctor/appointment/show";
    }


//    Login

    @GetMapping("/presentation/usuarios/login")
    public String showLoginPage() {
        return "/presentation/usuarios/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Obtener el usuario autenticado desde SecurityContext
        /*Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }*/

        String username = "Jperez";
        // Guardar en la sesión si no está presente
        if (session.getAttribute("username") == null) {
            session.setAttribute("username", username);
        }

        // Pasar el nombre de usuario a la vista
        model.addAttribute("username", username);
        return "/presentation/fragments/fragments";
    }







}

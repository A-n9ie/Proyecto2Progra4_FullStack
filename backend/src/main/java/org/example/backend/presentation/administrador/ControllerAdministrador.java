package org.example.backend.presentation.administrador;


import org.example.backend.logic.Medico;
import org.example.backend.logic.ServiceDoctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@org.springframework.stereotype.Controller("administrador")
public class ControllerAdministrador {
    @Autowired
    private ServiceDoctor serviceDoctor;

    @GetMapping("/admin/management")
    public String mostrarManagement() {
        return "presentation/administrador/management";
    }

    @GetMapping("/filtrarDocs")
    public String filtrarDoctores(@RequestParam(value = "doctor", required = false) String doctor,
                                  Model model) {
        Iterable<Medico> doctoresFiltrados = serviceDoctor.medicosFindAll();

        model.addAttribute("Doctors", doctoresFiltrados);
        return "presentation/administrador/management.html";
    }



}

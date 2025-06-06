package org.example.backend.presentation.administrador;


import org.example.backend.logic.Medico;
import org.example.backend.logic.ServiceDoctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class ControllerAdministrador {
    @Autowired
    private ServiceDoctor serviceDoctor;

    @GetMapping("/medicos/pendientes")
    public List<Medico> getTodosMedicos() {
        return serviceDoctor.medicosFindAll(); // Trae todos los doctores, sin filtrar por aprobado
    }


    @PostMapping("/aprobar")
    @ResponseBody
    public ResponseEntity<?> aprobarDoctor(@RequestBody Map<String, Integer> payload) {
        Integer id = payload.get("id");
        if (id == null) {
            return ResponseEntity.badRequest().body("Falta id del doctor");
        }
        try {
            serviceDoctor.cambiarEstado(id, true);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al aprobar doctor");
        }
    }

}

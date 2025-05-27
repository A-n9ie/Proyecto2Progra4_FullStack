package org.example.backend.data;


import org.example.backend.logic.Paciente;
import org.example.backend.logic.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Paciente, Integer> {
    public Paciente findByCedula(String cedula);
    public Paciente findByUsuario(Usuario usuario);
    public Paciente save(Paciente paciente);
}

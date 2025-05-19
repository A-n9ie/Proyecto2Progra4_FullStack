package org.example.backend.data;

import org.example.backend.logic.HorariosMedico;
import org.example.backend.logic.Medico;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HorarioRepository extends CrudRepository<HorariosMedico, Integer> {

    List<HorariosMedico> findByMedicoId(Integer idMedico);
    void deleteAllByMedico(Medico medico);
}



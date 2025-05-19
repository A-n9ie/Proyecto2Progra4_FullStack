package org.example.backend.data;

import org.example.backend.logic.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findTopByOrderByIdDesc();
    Usuario findByUsuario(String username);

}


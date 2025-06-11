package org.example.backend.presentation.security;

import org.example.backend.data.UsuarioRepository;
import org.example.backend.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.example.backend.logic.Medico;
import org.example.backend.data.DoctorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> user = usuarioRepository.findByUsuario(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Usuario '" + username + "' no encontrado");
        }

        Usuario usuario = user.get();

        if ("Medico".equals(usuario.getRol())) {
            Medico medico = doctorRepository.findByUsuario(usuario);
            if (medico == null || !Boolean.TRUE.equals(medico.getAprobado())) {
                throw new UsernameNotFoundException("Su usuario no está aprobado, comuníquese con el administrador");
            }
        }

        return usuario;
    }





}
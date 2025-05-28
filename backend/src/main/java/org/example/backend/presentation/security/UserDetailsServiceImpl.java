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
        try {
            System.out.println("Cargando usuario: " + username);
            Optional<Usuario> user = usuarioRepository.findByUsuario(username);

            if (user.isEmpty()) {
                System.out.println("Usuario no encontrado: " + username);
                throw new UsernameNotFoundException("Usuario '" + username + "' no encontrado");
            }

            if ("Medico".equals(user.get().getRol())) {
                Medico medico = doctorRepository.findByUsuario(user.orElse(null));
                if (medico == null || !Boolean.TRUE.equals(medico.getAprobado())) {
                    throw new UsernameNotFoundException("Su usuario no está aprobado, comuníquese con el administrador");
                }
            }

            return new UserDetailsImp(user.orElse(null));

        } catch (UsernameNotFoundException e) {
            throw e; // ya lanzado arriba
        } catch (Exception e) {
            System.out.println("Error inesperado al cargar usuario: " + e.getMessage());
            throw new UsernameNotFoundException("Error interno al autenticar usuario");
        }
    }


}
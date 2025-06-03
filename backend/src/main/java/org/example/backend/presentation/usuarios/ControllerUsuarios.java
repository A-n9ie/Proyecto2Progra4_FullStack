package org.example.backend.presentation.usuarios;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.*;
import org.example.backend.DTO.PerfilMedicoDTO;
import org.example.backend.logic.*;
import org.example.backend.presentation.security.TokenService;
import org.example.backend.presentation.security.UserDetailsImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@RestController
@RequestMapping("/usuarios")
public class ControllerUsuarios {
    @Autowired
    private ServiceUser serviceUser;
    @Autowired
    private ServiceDoctor serviceDoctor;
    @Autowired
    private ServicePatient servicePatient;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/notAuthorized")
    public String error(Model model) {
        model.addAttribute("error", "Acceso NO autorizado");
        return "/presentation/error";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registrarUsuario(
            @RequestParam("cedula") String cedula,
            @RequestParam("nombre") String nombre,
            @RequestParam("username") String username,
            @RequestParam("clave") String clave,
            @RequestParam("rol") String rol,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

        try {
            if(rol.equals("Medico"))
                if(serviceDoctor.findDoctor(cedula) != null)
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor Already Exist");
            else
                if(servicePatient.findPatient(cedula) != null)
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Patient Already Exist");
            Usuario usuario = new Usuario();
            usuario.setUsuario(username);
            usuario.setClave(clave);
            usuario.setRol(rol);
            serviceUser.addUser(usuario);

            String nombreArchivo = null;
            if (foto != null && !foto.isEmpty()) {
                String originalFilename = foto.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
                nombreArchivo = username + "." + extension;
                serviceUser.guardarFoto(foto, nombreArchivo);
            }


            if(rol.equals("Medico"))
                serviceDoctor.addDoctor(new Medico(nombre, cedula, nombreArchivo, usuario));
            else
                servicePatient.addPatient(new Paciente(nombre, cedula, nombreArchivo, usuario));

            return ResponseEntity.ok("Usuario registrado con éxito.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir imagen.");
        }
    }

    @GetMapping("/foto/{nombreArchivo}")
    public ResponseEntity<Resource> verImagen(@PathVariable String nombreArchivo) throws IOException {
        String ruta = System.getProperty("user.dir") + "/fotosPerfil/" + nombreArchivo;
        File archivo = new File(ruta);
        if (!archivo.exists()) return ResponseEntity.notFound().build();

        Resource recurso = (Resource) new UrlResource(archivo.toURI());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(recurso);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario user) {
        System.out.println("ENTRÓ AL MÉTODO LOGIN");
        System.out.println("Login recibido: usuario=" + user.getUsuario() + ", clave=" + user.getClave());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsuario(), user.getClave()));

        String token = tokenService.generateToken(authentication);
        System.out.println("Token generado: " + token);

        return ResponseEntity.ok().body(Map.of("token", token));
    }

    @GetMapping("/verificar-token")
    public ResponseEntity<?> verificarToken() {
        return ResponseEntity.ok().body(Map.of("status", "token válido"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> userLogin(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String nombre = jwt.getClaim("name");
        Usuario usuario = serviceUser.getUser(nombre);
        Map<String, Object> usuarioLoggeado = new HashMap<>();
        usuarioLoggeado.put("id", usuario.getId());
        usuarioLoggeado.put("username", usuario.getUsuario());
        usuarioLoggeado.put("rol", usuario.getRol());

        return ResponseEntity.ok(usuarioLoggeado);
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

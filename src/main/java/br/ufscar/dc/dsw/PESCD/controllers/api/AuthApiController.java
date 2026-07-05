package br.ufscar.dc.dsw.PESCD.controllers.api;

import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.models.PerfilUsuario;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.repositories.UsuarioRepository;
import br.ufscar.dc.dsw.PESCD.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private static final Map<PerfilUsuario, String> DASHBOARD_POR_PERFIL = new LinkedHashMap<>();

    static {
        DASHBOARD_POR_PERFIL.put(PerfilUsuario.ROLE_ADMIN, "/admin/dashboard");
        DASHBOARD_POR_PERFIL.put(PerfilUsuario.ROLE_SECRETARIO, "/secretario/ofertas");
        DASHBOARD_POR_PERFIL.put(PerfilUsuario.ROLE_RESPONSAVEL, "/responsavel/ofertas");
        DASHBOARD_POR_PERFIL.put(PerfilUsuario.ROLE_SUPERVISOR, "/supervisor/dashboard");
        DASHBOARD_POR_PERFIL.put(PerfilUsuario.ROLE_ALUNO, "/aluno/dashboard");
    }

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthApiController(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {
        try {
            var authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            servletRequest.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context);

            var userDetails = (UserDetails) authentication.getPrincipal();
            var token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(toUsuarioAutenticadoResponse(authentication.getName(), token));
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthErrorResponse("FORBIDDEN", "login.error.disabled"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorResponse("UNAUTHORIZED", "login.error.invalid"));
        }
    }

    @GetMapping("/me")
    public UsuarioAutenticadoResponse me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RecursoNaoEncontradoException();
        }
        return toUsuarioAutenticadoResponse(authentication.getName(), null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    private UsuarioAutenticadoResponse toUsuarioAutenticadoResponse(String username, String token) {
        var usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(RecursoNaoEncontradoException::new);
        var perfis = listarPerfisOrdenados(usuario);
        var acessos = perfis.stream()
                .map(perfil -> new AcessoPerfilResponse(perfil.name(), DASHBOARD_POR_PERFIL.get(perfil)))
                .toList();
        var dashboardPrincipal = acessos.isEmpty() ? null : acessos.getFirst().url();
        return new UsuarioAutenticadoResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                perfis.stream().map(PerfilUsuario::name).toList(),
                dashboardPrincipal,
                acessos,
                token);
    }

    private List<PerfilUsuario> listarPerfisOrdenados(UsuarioModel usuario) {
        var perfisUsuario = usuario.getPerfis().stream()
                .map(perfil -> perfil.getNome())
                .toList();
        return DASHBOARD_POR_PERFIL.keySet().stream()
                .filter(perfisUsuario::contains)
                .toList();
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record UsuarioAutenticadoResponse(
            UUID id,
            String username,
            String nomeCompleto,
            String email,
            List<String> perfis,
            String dashboardPrincipal,
            List<AcessoPerfilResponse> acessos,
            String token
    ) {
    }

    public record AcessoPerfilResponse(
            String perfil,
            String url
    ) {
    }

    public record AuthErrorResponse(
            String error,
            String messageKey
    ) {
    }
}
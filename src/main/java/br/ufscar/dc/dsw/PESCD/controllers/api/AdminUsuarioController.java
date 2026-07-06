package br.ufscar.dc.dsw.PESCD.controllers.api;

import br.ufscar.dc.dsw.PESCD.dtos.AdminUsuarioForm;
import br.ufscar.dc.dsw.PESCD.dtos.UsuarioDTO;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.PerfilUsuario;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
        var usuario = usuarioService.criarUsuarioAdmin(toForm(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(usuario));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        var usuarios = usuarioService.listarUsuariosAdmin().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(toDto(usuarioService.buscarPorId(id)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioDTO dto) {

        var usuario = usuarioService.atualizarUsuarioAdmin(id, toForm(dto));
        return ResponseEntity.ok(toDto(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @AuthenticationPrincipal UserDetails administradorLogado,
            @PathVariable UUID id) {

        usuarioService.excluirUsuarioAdmin(id, administradorLogado.getUsername());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ValidacaoNegocioException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(ValidacaoNegocioException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessageKey()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegridade(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", "admin.usuario.error.exclusao.com.vinculos"));
    }

    private AdminUsuarioForm toForm(UsuarioDTO dto) {
        var form = new AdminUsuarioForm();
        form.setNomeCompleto(dto.getNomeCompleto());
        form.setEmail(dto.getEmail());
        form.setUsername(dto.getUsername());
        form.setPassword(dto.getPassword());
        form.setPerfil(extractPerfil(dto));
        form.setEnabled(dto.isEnabled());
        return form;
    }

    private PerfilUsuario extractPerfil(UsuarioDTO dto) {
        if (dto.getPerfis() == null || dto.getPerfis().isEmpty()) {
            throw new ValidacaoNegocioException("admin.usuario.error.perfil.invalido");
        }
        return dto.getPerfis().get(0);
    }

    private UsuarioDTO toDto(UsuarioModel usuario) {
        var dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setUsername(usuario.getUsername());
        dto.setEnabled(usuario.isEnabled());
        dto.setPerfis(usuario.getPerfis().stream()
                .map(perfil -> perfil.getNome())
                .toList());
        return dto;
    }
}

package dev.cepex.Cepex.Controller;

import dev.cepex.Cepex.Dto.UsuarioDTO;
import dev.cepex.Cepex.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // NOVO IMPORT
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class GerenciaController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>> listarPaginado(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<UsuarioDTO> paginaDeUsuarios = usuarioService.listarPaginado(page, size);
        return ResponseEntity.ok(paginaDeUsuarios);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO salvo = usuarioService.salvar(usuarioDTO);
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        usuarioDTO.setId(id);
        UsuarioDTO salvo = usuarioService.salvar(usuarioDTO);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Integer id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
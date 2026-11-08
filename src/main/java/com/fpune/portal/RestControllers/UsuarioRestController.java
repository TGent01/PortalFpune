
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Usuario;
import com.fpune.portal.services.UsuarioService;
import com.fpune.portal.status.UsuarioStatusEnum;
import java.net.URI;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    private UsuarioStatusEnum status;
    
    @GetMapping
    public ResponseEntity<ArrayList<Usuario>> getUsuarios(){
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }
    
    @PostMapping
    public ResponseEntity<Usuario> postUsuario(@RequestBody Usuario usuario){
        int r=usuarioService.insertarUsuario(usuario);
        if(r==status.USUARIO_CREADO.getCodigo()){
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();
        
        // 2. Retornar el estado 201 CREATED, incluir la URI en el header Location y el usuario en el cuerpo
        return ResponseEntity.created(location).body(usuario);
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable Long id){
        Usuario usuario=usuarioService.obtenerUsuario(id);
        
        return usuario==null?ResponseEntity.notFound().build():ResponseEntity.ok(usuario);
    }
    
    @GetMapping("/ci")
    public ResponseEntity<Usuario> getUsuarioByCorreo(@RequestParam String correo){
        Usuario usuario=usuarioService.obtenerUsuarioPorCorreo(correo);
        
        return usuario==null?ResponseEntity.notFound().build():ResponseEntity.ok(usuario);
    }
    
    @PutMapping()
    public ResponseEntity<?> pathUsuario(@RequestBody Usuario u){
        int r=usuarioService.modificarUsuario(u);
        if(r==status.USUARIO_EDITADO.getCodigo()){
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado");
        }else if(r==status.USUARIO_NO_EXISTE.getCodigo()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe");
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error aL procesar el usuario");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable long id){
        int r=usuarioService.eliminarUsuario(id);
        if(r==status.USUARIO_NO_EXISTE.getCodigo()){return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usario no existe");}
        else if(r==status.USUARIO_ELIMINADO.getCodigo()){return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);}
        return ResponseEntity.badRequest().build();
        
    }
}

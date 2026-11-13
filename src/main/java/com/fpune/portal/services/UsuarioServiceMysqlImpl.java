
package com.fpune.portal.services;

import com.fpune.portal.controllers.UsuarioJpaController;
import com.fpune.portal.controllers.UsuarioJpaController;
import com.fpune.portal.entity.Rol;
import com.fpune.portal.entity.Usuario;
import com.fpune.portal.status.UsuarioStatusEnum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password4j.BcryptPassword4jPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceMysqlImpl implements UsuarioService{
    private UsuarioJpaController usuarioJpaController=new UsuarioJpaController();
    private UsuarioStatusEnum status;

    @Override
    public ArrayList<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarioJpaController.findUsuarioEntities());
    }

    @Override
    public Usuario obtenerUsuario(long id) {
        return usuarioJpaController.findUsuario(id);
    }

    @Override
    public int insertarUsuario(Usuario u) {
        // Rechazar si falta CUALQUIERA de los campos obligatorios
        if(estaVacio(u.getCi()) || estaVacio(u.getCorreo()) || estaVacio(u.getPassword())
                || estaVacio(u.getNombre()) || estaVacio(u.getApellido())){
            return status.USUARIO_NO_PROCESABLE.getCodigo();
        }
        // Valores por defecto
        if(u.getIsEnable()==null){
            u.setIsEnable(true);
        }
        u.setFechaCreacion(new Date()); // la fecha de alta siempre la pone el servidor
        if(u.getRolList()==null || u.getRolList().isEmpty()){
            u.setRolList(new ArrayList<>(List.of(new Rol(1)))); // rol Solicitante
        }
        String passwordHash=new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(passwordHash);
        try {
            usuarioJpaController.create(u);
            return status.USUARIO_CREADO.getCodigo();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return status.USUARIO_NO_PROCESABLE.getCodigo();
            
        }
    }

    @Override
    public int modificarUsuario(Usuario u) {
        if(u.getId()==null){
            return status.USUARIO_NO_PROCESABLE.getCodigo();
        }
        Usuario oldUsuario=usuarioJpaController.findUsuario(u.getId());
        if(oldUsuario==null){
            return status.USUARIO_NO_EXISTE.getCodigo();
        }
        u.setPassword(oldUsuario.getPassword());
        u.setCorreo(u.getCorreo()==null?oldUsuario.getCorreo():u.getCorreo());
        u.setCi(u.getCi()==null?oldUsuario.getCi():u.getCi());
        u.setNombre(u.getNombre()==null?oldUsuario.getNombre():u.getNombre());
        u.setApellido(u.getApellido()==null?oldUsuario.getApellido():u.getApellido());
        u.setFechaCreacion(u.getFechaCreacion()==null?oldUsuario.getFechaCreacion():u.getFechaCreacion());
        u.setIsEnable(u.getIsEnable()==null?oldUsuario.getIsEnable():u.getIsEnable());
        u.setVinculoInstitucional(u.getVinculoInstitucional()==null?oldUsuario.getVinculoInstitucional():u.getVinculoInstitucional());
        u.setRolList(u.getRolList()==null?oldUsuario.getRolList():u.getRolList());
        try {
            usuarioJpaController.edit(u);
            return status.USUARIO_EDITADO.getCodigo();
        } catch (Exception e) {
            return status.USUARIO_NO_PROCESABLE.getCodigo();
        }

    }

    @Override
    public int eliminarUsuario(Long id) {
        if(usuarioJpaController.findUsuario(id)==null){
            return status.USUARIO_NO_EXISTE.getCodigo();
        }
        try {
            usuarioJpaController.destroy(id);
        } catch (Exception e) {
            return status.USUARIO_NO_PROCESABLE.getCodigo();
        }
        
        return status.USUARIO_ELIMINADO.getCodigo();
    }

    @Override
    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioJpaController.findUsuarioByCorreo(correo);
    }

    // true si el texto es null o solo tiene espacios
    private static boolean estaVacio(String s) {
        return s == null || s.isBlank();
    }
    
    
}

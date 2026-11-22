
package com.fpune.portal.services;

import com.fpune.portal.entity.Usuario;
import java.util.ArrayList;


public interface UsuarioService {
    public ArrayList<Usuario> listarUsuarios();
    public Usuario obtenerUsuario(long id);
    public int insertarUsuario(Usuario u);
    public int modificarUsuario(Usuario u);
    public int eliminarUsuario(Long id);
    public Usuario obtenerUsuarioPorCorreo(String correo);
}

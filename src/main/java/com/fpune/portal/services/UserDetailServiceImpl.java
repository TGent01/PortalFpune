
package com.fpune.portal.services;

import com.fpune.portal.entity.Rol;
import com.fpune.portal.entity.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService{
    
    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario=usuarioService.obtenerUsuarioPorCorreo(username);
        if(usuario==null){
            throw new UsernameNotFoundException("El usuario no existe");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Rol rol : usuario.getRolList()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
        }

        // 3. Devolvemos el objeto User que Spring Security sí entiende
        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getIsEnable(), 
                true, true, true,
                authorities
        );
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.controllers.UsuarioJpaController;
import com.fpune.portal.dtos.AuthRequestDTO;
import com.fpune.portal.dtos.AuthResponseDTO;
import com.fpune.portal.entity.Usuario;
import com.fpune.portal.security.JwtUtil;
import com.fpune.portal.services.UserDetailServiceImpl;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author marti
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> crearTokenDeAuthenticacion(@RequestBody AuthRequestDTO request){
        Usuario usuarioopt=new UsuarioJpaController().findUsuarioByCorreo(request.getEmail());
        if(usuarioopt==null){
            return ResponseEntity.status(401).body("Correo o contraseña incorrectos");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        } catch (BadCredentialsException BCEx) {
            return ResponseEntity.status(401).body("Correo o contraseña incorrectos");
        }
        final UserDetails userDetails=userDetailServiceImpl.loadUserByUsername(request.getEmail());
        final String token=jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
    
}

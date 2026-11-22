package com.fpune.portal.security;


import com.fpune.portal.services.UserDetailServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetailsService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Si no hay token, dejamos que Spring Security se encargue (bloqueará si la ruta es privada)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // Intentamos extraer el usuario. Aquí es donde JWT lanza los errores si el token es falso o expiró.
            username = jwtUtil.extractUsername(jwt);

        } catch (ExpiredJwtException e) {
            // Error 1: El token es válido pero su tiempo de vida ya terminó
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("La sesión ha expirado");
            return; // Cortamos el proceso aquí

        } catch (SignatureException | MalformedJwtException e) {
            // Error 2: Alguien alteró el token o está mal escrito
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("La sesión no es válida");
            return; // Cortamos el proceso aquí

        } catch (Exception e) {
            // Error 3: Cualquier otro fallo no previsto
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Error al procesar el token de seguridad");
            return; // Cortamos el proceso aquí
        }

        // Si pasó sin errores, continuamos con la validación del usuario normal
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
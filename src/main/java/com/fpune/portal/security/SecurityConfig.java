
package com.fpune.portal.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password4j.BcryptPassword4jPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuth;
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig){
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // AGREGAR ESTA LÍNEA: Permite que el navegador haga la validación previa de CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                
        
                .requestMatchers(HttpMethod.POST,"/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/usuarios/**").hasRole("Admin")
                .requestMatchers(HttpMethod.POST,"/usuarios").hasRole("Admin")
                .requestMatchers(HttpMethod.PUT,"/usuarios").hasRole("Admin")
                .requestMatchers(HttpMethod.DELETE,"/usuarios/**").hasRole("Admin")

               .requestMatchers(HttpMethod.GET,"/categorias").permitAll()
                .requestMatchers(HttpMethod.GET, "/categorias/todas").hasRole("Admin")
                .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/categorias").hasRole("Admin")
                .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("Admin")

                // Página de error de Spring: si no se permite, cualquier excepción (500)
                // o error llega al cliente como 403 por el denyAll() del final.
                .requestMatchers("/error").permitAll()

                .requestMatchers(HttpMethod.GET, "/estados/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/estados").hasRole("Admin")
                .requestMatchers(HttpMethod.PUT, "/estados").hasRole("Admin")
                .requestMatchers(HttpMethod.GET,"/prioridades/**").hasRole("Admin")
                .requestMatchers(HttpMethod.GET, "/tipos-solicitud/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/tipos-solicitud").hasRole("Admin")
                .requestMatchers(HttpMethod.PUT, "/tipos-solicitud").hasRole("Admin")

                 // Solicitudes:
                .requestMatchers(HttpMethod.GET, "/solicitudes/ticket/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/solicitudes/**").hasRole("Admin")
                 .requestMatchers(HttpMethod.PUT, "/solicitudes/**").hasRole("Admin")
                .requestMatchers(HttpMethod.POST, "/solicitudes").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/solicitudes/**").hasRole("Admin")
                // BUENA PRÁCTICA: Rechazar cualquier otra ruta que no esté especificada arriba
                    
                    // Evidencias: 
                .requestMatchers(HttpMethod.POST, "/evidencias/ticket/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/evidencias/ticket/**").hasRole("Admin")
                .requestMatchers(HttpMethod.GET, "/evidencias/**").hasRole("Admin")
                .requestMatchers(HttpMethod.DELETE, "/evidencias/**").hasRole("Admin")
                // Estadísticas (dashboard del administrador)
                .requestMatchers(HttpMethod.GET, "/estadisticas/**").hasRole("Admin")
                    
                    
                 .requestMatchers(HttpMethod.GET, "/datos-abiertos/**").permitAll()
                .anyRequest().denyAll()
            

            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuth, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. ¿Quién puede conectarse? (Tus URLs de Next.js en desarrollo y Vercel en producción)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://127.0.0.1:5500"
        ));
        
        
        // 2. ¿Qué métodos HTTP están permitidos?
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 3. ¿Qué cabeceras (Headers) permitimos que nos envíen?
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition"));
        
        // 4. Permitir que el frontend lea la respuesta
        configuration.setAllowCredentials(true);

        // 5. Aplicar esta configuración a todas las rutas ("/api/**")
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

package com.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/db-console/**", "/greeting/**", "/login", "/register", "/home", "/js/**", "/pet", "/api/**").permitAll()
                        .anyRequest().authenticated());
                //.formLogin(formLogin -> formLogin
                //        .loginPage("/login.html")
                //        .loginProcessingUrl("/perform_login")
                //        .defaultSuccessUrl("/home.html", true)
                //)
                //.logout(logout -> logout
                //      .logoutSuccessUrl("/logout")
                //      .permitAll()
                //);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // Erlaube CORS-Requests für alle Pfade
                    .allowedOrigins("http://localhost:3000", "https://virtual-pet-bcky.onrender.com") // URL des Frontends
                    .allowedMethods("GET", "POST", "PUT", "DELETE") // Erlaubte Methoden
                    .allowedHeaders("*") // Erlaubte Headers
                    .allowCredentials(true); // Cookies und Authentifizierungsinformationen erlauben
        }
    }
}
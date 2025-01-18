package com.gamelogy.screenmatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //Permite todas las rutas.
                .allowedOrigins("http://127.0.0.1:5500") // Dominios permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT"); // Métodos permitidos
                //.allowedHeaders("*") // Headers permitidos
                //.allowCredentials(true) // Permitir credenciales
                //.maxAge(3600); // Tiempo máximo de cache en segundos
    }
}

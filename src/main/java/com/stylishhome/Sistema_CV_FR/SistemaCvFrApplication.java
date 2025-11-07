package com.stylishhome.Sistema_CV_FR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase principal de la aplicación Sistema-CV
 * Configura Spring Boot y la aplicación web
 */
@SpringBootApplication
public class SistemaCvFrApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SistemaCvFrApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SistemaCvFrApplication.class, args);
    }
}
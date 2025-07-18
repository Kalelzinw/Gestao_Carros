package com.example.gestao.carros.config; // Ou o pacote de sua preferência

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esta linha é a mágica que resolve o problema.
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations("file:./uploads/");
    }
}
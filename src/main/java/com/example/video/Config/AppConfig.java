package com.example.video.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

//@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

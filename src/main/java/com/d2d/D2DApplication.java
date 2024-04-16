package com.d2d;



import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class D2DApplication {
    private static final Logger logger = LoggerFactory.getLogger(D2DApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(D2DApplication.class, args);
        logger.info("*******D2D APPLICATION STARTED********");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper( );
    }


}

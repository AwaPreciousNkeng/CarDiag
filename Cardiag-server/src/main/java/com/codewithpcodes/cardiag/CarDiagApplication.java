package com.codewithpcodes.cardiag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CarDiagApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarDiagApplication.class, args);
    }

}

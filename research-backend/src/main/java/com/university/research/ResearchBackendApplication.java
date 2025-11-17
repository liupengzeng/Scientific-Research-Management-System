package com.university.research;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.university.research.system.mapper")
public class ResearchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResearchBackendApplication.class, args);
    }

}

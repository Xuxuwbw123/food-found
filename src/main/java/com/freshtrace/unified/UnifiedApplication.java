package com.freshtrace.unified;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.freshtrace.unified.mapper")
public class UnifiedApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnifiedApplication.class, args);
    }
}

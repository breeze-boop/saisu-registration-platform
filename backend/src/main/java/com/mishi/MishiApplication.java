package com.mishi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.mishi.mapper")
@SpringBootApplication
public class MishiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MishiApplication.class, args);
    }
}

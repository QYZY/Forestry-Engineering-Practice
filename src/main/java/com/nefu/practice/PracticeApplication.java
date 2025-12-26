package com.nefu.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
public class PracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeApplication.class, args);
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(4);
    }

}

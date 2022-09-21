package com.notsay.dingtalkalarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DingtalkalarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(DingtalkalarmApplication.class, args);
    }

}

package com.example.baas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author John
 * @create 2019/7/10 13:49
 */
@SpringBootApplication
@EnableConfigurationProperties
public class RunApplication{
    public static void main(String[] args){
        SpringApplication.run(RunApplication.class,args);
    }
}

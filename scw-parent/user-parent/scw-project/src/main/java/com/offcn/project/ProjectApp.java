package com.offcn.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.offcn.project.mapper")
@EnableSwagger2
public class ProjectApp {
    public static void main(String[] args) {
        SpringApplication.run(ProjectApp.class);
    }
}

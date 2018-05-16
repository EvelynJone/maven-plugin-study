package com.evelyn.mavenplugin.study.launch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 使用capsule-简单版
 */
@EnableEurekaServer
@SpringBootApplication
public class BuildingAutoApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BuildingAutoApplication.class).web(true).run(args);
    }

}

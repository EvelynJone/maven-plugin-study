package com.evelyn.mavenplugin.study.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 功能说明：TODO
 *
 * @auther by zhaoxl
 * @return <br/>
 * 修改历史：<br/>
 * 1.[2018年05月16日上午17:25]
 */
@EnableEurekaServer
@SpringBootApplication
public class CustomBuildsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomBuildsApplication.class,args);
    }
}

package com.hospital.permissiontracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
public class PermissionTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionTrackingApplication.class, args);
    }

}

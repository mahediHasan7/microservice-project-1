package org.mahedi.photoappapiuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class PhotoAppApiUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoAppApiUserApplication.class, args);
    }

}

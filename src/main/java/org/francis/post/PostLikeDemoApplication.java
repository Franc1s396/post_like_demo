package org.francis.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PostLikeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostLikeDemoApplication.class, args);
    }

}

package com.github.millefoglie.tagithubrepos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = { TaGithubReposApplication.class })
public class TaGithubReposApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaGithubReposApplication.class, args);
    }

}

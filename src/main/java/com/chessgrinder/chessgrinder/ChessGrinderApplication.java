package com.chessgrinder.chessgrinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.*;

@SpringBootApplication
public class ChessGrinderApplication {

    public static void main(String[] args) {
        System.out.println("Hello World");
        SpringApplication.run(ChessGrinderApplication.class, args);
    }
}

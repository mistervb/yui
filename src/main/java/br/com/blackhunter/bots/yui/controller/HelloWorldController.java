package br.com.blackhunter.bots.yui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloWorldController {
    @GetMapping
    public ResponseEntity<String> doHelloWorld() {
        return ResponseEntity.ok("Hello World!");
    }
}

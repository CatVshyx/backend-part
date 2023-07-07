package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String login, @RequestParam String password){

        return null;
    }
    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestParam String token){
        return null;
    }

}

package com.example.backend.controller;

import com.example.backend.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    private AuthorizationService authorizationService;
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String login, @RequestParam String password)  {
        try {
            return authorizationService.login(login, password);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return new ResponseEntity<>("Missing refresh token",HttpStatus.BAD_REQUEST);
        }
        String token = authHeader.substring(7);

        return authorizationService.refresh(token);

    }
}

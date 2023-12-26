package com.example.backend.controller;

import com.example.backend.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {
    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody HashMap<String,String> body)  {
        try {
            String login = body.get("login");
            String password = body.get("password");
            if (login == null || password == null)
                throw new IllegalArgumentException("Missing login or password");
            return authorizationService.login(login, password);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(Map.of("error","Bad login or password"),HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/me")
    public ResponseEntity<Object> getMe(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return new ResponseEntity<>(Map.of("error","Missing Bearer "),HttpStatus.BAD_REQUEST);
        }
        final String jwtToken = authHeader.substring(7);

        return ResponseEntity.ok(authorizationService.getUserByToken(jwtToken));
    }
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody HashMap<String,String> body){
        String token = body.get("refresh");
        if(token == null)
            throw new IllegalArgumentException("Missing refresh token");

        return authorizationService.refresh(token);
    }
}

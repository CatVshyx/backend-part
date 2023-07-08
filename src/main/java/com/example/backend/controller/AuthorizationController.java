package com.example.backend.controller;

import com.example.backend.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import javax.security.auth.login.AccountExpiredException;


@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    private AuthorizationService authorizationService;
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String login, @RequestParam String password)  {
        try {
            return ResponseEntity.ok(authorizationService.login(login, password));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AccountExpiredException e) {
            return new ResponseEntity<>("This account is expired",HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return new ResponseEntity<>("Missing refresh token",HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);

        return authorizationService.refresh(token);

    }
    @GetMapping("/hi")
    public String hi(){
        return "hi";
    }
}

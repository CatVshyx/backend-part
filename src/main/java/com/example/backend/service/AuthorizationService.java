package com.example.backend.service;

import com.example.backend.config.JwtService;
import com.example.backend.dao.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Map;

@Service
public class AuthorizationService {
    @Autowired
    private UserService userService;



    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthorizationService(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    public ResponseEntity<Object> login(String login, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login,password));
        User u = userService.findUserByLogin(login);

        if (u.getExpiredAt() != null && u.getExpiredAt().isBefore(LocalDate.now()))
            return new ResponseEntity<>("This account is expired, please contact the admin", HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(Map.of(
                "access",jwtService.generateToken(User.toUserDetails(u),1),
                "refresh", jwtService.generateToken(User.toUserDetails(u),1))
        );
    }
    public ResponseEntity<Object> refresh(String token){
        try{
            jwtService.isTokenExpired(token);

            String login = jwtService.extractLogin(token);
            User user = userService.findUserByLogin(login);

            String access = jwtService.generateToken(User.toUserDetails(user),3);
            return new ResponseEntity<>(Map.of("access",access,"refresh",token), HttpStatus.OK);
        }catch (ExpiredJwtException e){
            return new ResponseEntity<>("Refresh token was expired",  HttpStatus.UNAUTHORIZED);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }


    }
}

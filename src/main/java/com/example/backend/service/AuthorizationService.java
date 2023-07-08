package com.example.backend.service;

import com.example.backend.config.JwtService;
import com.example.backend.dao.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountExpiredException;
import java.time.LocalDate;
import java.util.HashMap;
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
    public Map<String, String> login(String login, String password) throws BadCredentialsException,AccountExpiredException {
        Authentication au = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login,password));
        System.out.println(au.getAuthorities() + " " + au.isAuthenticated());
        System.out.println(au.getPrincipal());
        User u = userService.findUserByLogin(login);
        return Map.of(
                "access",jwtService.generateToken(User.toUserDetails(u),1),
                "refresh", jwtService.generateToken(User.toUserDetails(u),1)
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
            return new ResponseEntity<>("Refresh token was expired, please sign in again", HttpStatusCode.valueOf(401));
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>("User not found", HttpStatusCode.valueOf(404));
        }


    }
}

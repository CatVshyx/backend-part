package com.example.backend.controller;

import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class MainController {
    @Autowired
    private UserService userService;


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody HashMap<String,String> map){
        String password = map.get("current_password");
        String newPassword = map.get("new_password");

        if (password == null || newPassword == null)
            return new ResponseEntity<>("Data sent invalid", HttpStatus.BAD_REQUEST);
        try{
            return userService.changePassword(token.substring(7),password,newPassword);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/showSample")
    public ResponseEntity<String> showSample(){
        return null;
    }


    @GetMapping("/downloadFile")
    public ResponseEntity<String> download(){
        return null;
    }

    @GetMapping("/")
    public ResponseEntity<String> get(){
        return null;
    }
}

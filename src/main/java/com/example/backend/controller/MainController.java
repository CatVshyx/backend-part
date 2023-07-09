package com.example.backend.controller;

import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class MainController {
    @Autowired
    private UserService userService;


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody HashMap<String,String> map){
        if (!map.containsKey("current_password") && !map.containsKey("new_password"))
            return new ResponseEntity<>("Data sent invalid", HttpStatus.BAD_REQUEST);
        return userService.changePassword(token,map);
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

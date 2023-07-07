package com.example.backend.controller;

import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController {
    @Autowired
    private UserService userService;
    @GetMapping("/showSample")
    public ResponseEntity<String> showSample(){
        return null;
    }


    @GetMapping("/downloadFile")
    public ResponseEntity<String> download(){
        return null;
    }
}

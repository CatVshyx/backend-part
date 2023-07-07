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
    @GetMapping("/hi")
    public String sayHi(){
        return "hello";
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String,String> request){
        return null;
    }

    @GetMapping("/getUsers")
    public String getUsers(){
        return userService.getUsers();
    }
    @PostMapping("/addUser")
    public ResponseEntity<String> addUsers(@RequestBody Map<String,String> body){
       return new ResponseEntity<>(HttpStatus.OK);

    }

}

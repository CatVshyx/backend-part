package com.example.backend.controller;


import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;


    @PostMapping("/addForm")
    public ResponseEntity<String> addForm(){
        return null;
    }
    @PostMapping("/editForm")
    public ResponseEntity<String> editForm(){
        return null;
    }
    @DeleteMapping("/deleteForm")
    public ResponseEntity<String> deleteForm(){
        return null;
    }
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody Map<String,String> request){
        if (!request.containsKey("login") || !request.containsKey("password") || !request.containsKey("name"))
            return new ResponseEntity<>("Check all required data: login, password and name",HttpStatusCode.valueOf(400));
        return userService.addUser(request.get("login"),request.get("password"),request.get("name"));
    }
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam String login){
        //TODO Admin can`t delete himself
        return new ResponseEntity<>(userService.deleteUser(login) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/getUsers")
    public String getUsers(){
        return userService.getUsers();
    }
}

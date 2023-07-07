package com.example.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @PostMapping("/addForm")
    public ResponseEntity<String> addForm(){
        return null;
    }
    @PostMapping("/editForm")
    public ResponseEntity<String> editForm(){
        return null;
    }
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestParam String login, @RequestParam String password){

        return null;
    }
    @DeleteMapping()
}

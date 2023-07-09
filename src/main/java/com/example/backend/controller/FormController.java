package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form")
public class FormController {
    @PostMapping("/add")
    public ResponseEntity<String> addForm(){
        return null;
    }
    @PostMapping("/edit")
    public ResponseEntity<String> editForm(){
        return null;
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteForm(){
        return null;
    }
}

package com.example.backend.controller;

import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.HashMap;

@RestController
public class HomeController  {
//    @Value("${spring.resources.static-locations}")
//    private String resources;
    @Autowired
    private UserService userService;

    @GetMapping(value = {"/{path:^(?!api).*$}"} , produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> index() throws IOException {

        return null;
//        ClassPathResource indexHtml = new ClassPathResource("static/index.html");
//       System.out.println(indexHtml.getPath());
//        FileSystemResource indexHtml = new FileSystemResource(resources+"index.html" );
//        return ResponseEntity.ok()
//                .body(new InputStreamResource(indexHtml.getInputStream()));
//    }


//    @GetMapping(value = {"/favicon.ico"} , produces = "image/x-icon")
//    public ResponseEntity<Resource> getFavicon() throws IOException {
//
//        FileSystemResource favicon = new FileSystemResource(resources + "favicon.ico");
//        System.out.println(favicon.getPath());
//        return ResponseEntity.ok()
//                .body(new InputStreamResource(favicon.getInputStream()));
//    }
    @PostMapping("/api/change-password")
    public ResponseEntity<Object> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody HashMap<String,String> map){
        String password = map.get("current_password");
        String newPassword = map.get("new_password");

        if (password == null || newPassword == null)
            throw new IllegalArgumentException();

        return userService.changePassword(token.substring(7),password,newPassword);
    }
}

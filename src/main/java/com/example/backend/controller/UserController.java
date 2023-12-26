package com.example.backend.controller;


import com.example.backend.dao.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Object> createUser(@RequestBody Map<String,String> request){
        if (!request.containsKey("login") || !request.containsKey("password") || !request.containsKey("name"))
            throw new IllegalArgumentException("Check all required data: login, password and name");
        int expiration = Integer.parseInt(request.get("expiration"));
        return userService.createUser(request.get("login"),request.get("password"),request.get("name"),expiration);
    }
    @PatchMapping("/edit")
    public ResponseEntity<Object> editUser(@RequestBody Map<String, String> map){
        int id = Integer.parseInt(map.get("id"));
        String login = map.get("login");
        String name = map.get("name");
        userService.editUser(id,login,name);

        return ResponseEntity.ok().build();
    }
    @PatchMapping("/extend-expiration")
    public ResponseEntity<Object> extendExpiration(@RequestBody HashMap<String, String> map){
        int id = Integer.parseInt(map.get("id"));
        int days = Integer.parseInt(map.get("days"));
        userService.extendUserDays(id, days);

        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(@RequestParam int id){
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam int id){
        return ResponseEntity.ok(userService.resetPassword(id,12));
    }
    @GetMapping(path = {"","/"})
    public List<User> getUsers(){
        return userService.getUsers();
    }

}

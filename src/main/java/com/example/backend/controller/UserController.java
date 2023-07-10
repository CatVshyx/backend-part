package com.example.backend.controller;


import com.example.backend.dao.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody Map<String,String> request){
        if (!request.containsKey("login") || !request.containsKey("password") || !request.containsKey("name"))
            return new ResponseEntity<>("Check all required data: login, password and name",HttpStatusCode.valueOf(400));
        try{
            return userService.addUser(request.get("login"),request.get("password"),request.get("name"),request.get("expiration"));
        }catch (NumberFormatException e){
            return new ResponseEntity<>("Please enter int numbers to add days",HttpStatus.BAD_REQUEST);
        }

    }
    @PatchMapping("/edit")
    public ResponseEntity<String> editUser(@RequestBody HashMap<String, String> map){
        try{
            int id = Integer.parseInt(map.get("id"));
            String login = map.get("login");
            String name = map.get("name");

            if(login == null && name == null)
                return ResponseEntity.ok("User was updated");
            return userService.editUser(id,login,name);
        }catch (NumberFormatException | NullPointerException e){
            return new ResponseEntity<>("Invalid data received",HttpStatus.BAD_REQUEST);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PatchMapping("/extend-expiration")
    public ResponseEntity<String> extendExpiration(@RequestBody HashMap<String, String> map){
        try{
            int id = Integer.parseInt(map.get("id"));
            int days = Integer.parseInt(map.get("days"));
            return userService.extendUserDays(id, days);
        }catch (Exception e){
            return new ResponseEntity<>("Invalid data received",HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam int id){
        try{
            return userService.deleteUser(id);
        }
        catch (UsernameNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam int id){
        try{
            return ResponseEntity.ok(userService.resetPassword(id,12));
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/")
    public List<User> getUsers(){
        return userService.getUsers();
    }
}
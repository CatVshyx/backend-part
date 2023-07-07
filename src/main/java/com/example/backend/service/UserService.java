package com.example.backend.service;

import com.example.backend.additional.Response;
import com.example.backend.dao.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Value("${admin.login}")
    private String adminLogin;
    @Value("${admin.password}")
    private String adminPassword;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
    public String getUsers(){
        return userRepository.findAll().toString();
    }
    @EventListener(ApplicationReadyEvent.class)
    public void checkAdminOnStartup() {
        for(User user : userRepository.findAll()){
            if (user.isAdmin())
                return;
        }
        //TODO ENCODE PASSWORD BLYAT
        User user = new User(adminLogin,"root",adminPassword,null);
        user.setAdmin(true);
        System.out.println(user);
        userRepository.save(user);
    }

    public ResponseEntity<String> addUser(String login, String password, String name){
        //TODO ENCODE PASSWORD BLYAT
        for(User user : userRepository.findAll()){
            if (user.getLogin().equals(login))
                return new ResponseEntity<>("User with such login already exists", HttpStatus.BAD_REQUEST);
        }
        if (password.length() < 8)
            return new ResponseEntity<>("Password is too short",HttpStatus.BAD_REQUEST);

        User user = new User(login, name,password, LocalDate.now().plusMonths(1));
        userRepository.save(user);
        return new ResponseEntity<>("User is saved", HttpStatus.OK);
    }
    public boolean deleteUser(String login){
        Optional<User> optionalUser = userRepository.findByLogin(login);
        if (optionalUser.isEmpty() || optionalUser.get().isAdmin())
            return false;
        userRepository.delete(optionalUser.get());

        return true;
    }
}

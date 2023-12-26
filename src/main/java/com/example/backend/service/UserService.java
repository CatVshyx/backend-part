package com.example.backend.service;

import com.example.backend.dao.User;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${admin.login}")
    private String adminLogin;
    @Value("${admin.password}")
    private String adminPassword;
    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
    public List<User> getUsers(){
        return userRepository.findAll();
    }
    @EventListener(ApplicationReadyEvent.class)
    public void setAdminOnStartup() {
        for(User user : userRepository.findAll()){
            if (user.isAdmin())
                return;
        }

        User user = new User(adminLogin,"root",passwordEncoder.encode(adminPassword),null);
        user.setAdmin(true);
        userRepository.save(user);
    }

    public ResponseEntity<Object> createUser(String login, String password, String username,int expiration) throws NumberFormatException,EntityExistsException{
        for(User user : userRepository.findAll()){
            if (user.getLogin().equals(login))
                throw new EntityExistsException("Such user already exists");
        }
        if (password.length() < 8)
            return new ResponseEntity<>(Map.of("error","Password is too short"),HttpStatus.NOT_ACCEPTABLE);

        User user = new User(login, username,passwordEncoder.encode(password));
        expiration = expiration < 0 || expiration > 365 ? 30 : expiration;
        user.setExpiredAt(LocalDate.now().plusDays(expiration));

        return  ResponseEntity.ok(userRepository.save(user));
    }
    public void deleteUser(int id){
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(null));
        if (user.isAdmin())
            throw new IllegalArgumentException("admin can`t delete himself");

        userRepository.delete(user);
    }
    User findUserByLogin(String login) throws UsernameNotFoundException{
        return userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException(null));
    }
    public void editUser(int id, String login, String name) throws EntityNotFoundException{
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (name != null && !user.getName().equals(name)){
            if (userRepository.existsByName(name))
                throw new EntityExistsException("Such name is already defined");
            user.setName(name);
        }
        if (login != null && !user.getLogin().equals(login)){
            if(userRepository.existsByLogin(login))
                throw new EntityExistsException("Such login is already occupied");
            user.setLogin(login);
        }

        userRepository.save(user);
    }
    public void extendUserDays(int id, int days){
        Optional<User> optional = userRepository.findById(id);

        if (optional.isEmpty() || optional.get().isAdmin())
            throw new EntityNotFoundException("User not found");
        if (days < 0 || days > 365)
            throw new IllegalArgumentException("Only numbers from 0 - 365 are allowed");

        User user = optional.get();
        user.setExpiredAt(user.getExpiredAt().plusDays(days));
        userRepository.save(user);
    }

    public ResponseEntity<Object> changePassword(String token, String password, String newPassword) throws EntityNotFoundException{
        String login = jwtService.extractLogin(token);
        User user = userRepository.findByLogin(login).orElseThrow(EntityNotFoundException::new);

        if (!passwordEncoder.matches(password,user.getPassword()) || user.isAdmin())
            return new ResponseEntity<>(Map.of("error","Invalid password"), HttpStatus.UNAUTHORIZED);
        if (newPassword.length() < 8)
            return new ResponseEntity<>(Map.of("error","Password is less than 8 characters"),HttpStatus.CONFLICT);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public Map<String,String> resetPassword(int id, int charMax) throws EntityNotFoundException{
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (user.isAdmin())
            throw new UsernameNotFoundException(null);

        Random rd = new Random();
        char[] arr = new char[charMax];
        for(int i = 0; i < charMax; i++){
            arr[i] = CHARACTERS.charAt(rd.nextInt(256) % CHARACTERS.length());
        }

        String password = new String(arr);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return Map.of("login",user.getLogin(),"password",password);

    }
}

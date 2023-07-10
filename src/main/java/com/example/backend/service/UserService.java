package com.example.backend.service;

import com.example.backend.config.JwtService;
import com.example.backend.dao.User;
import com.example.backend.repository.UserRepository;
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
    //TODO SQL injections
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
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
    public List<User> getUsers(){
        List<User> list = new ArrayList<>();
        userRepository.findAll().forEach(list::add);
        return list;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void checkAdminOnStartup() {
        for(User user : userRepository.findAll()){
            if (user.isAdmin())
                return;
        }

        User user = new User(adminLogin,"root",passwordEncoder.encode(adminPassword),null);
        user.setAdmin(true);
        System.out.println(user);
        userRepository.save(user);
    }

    public ResponseEntity<String> addUser(String login, String password, String name,String exp) throws NumberFormatException{

        for(User user : userRepository.findAll()){
            if (user.getLogin().equals(login))
                return new ResponseEntity<>("User with such login already exists", HttpStatus.CONFLICT);
        }
        if (password.length() < 8)
            return new ResponseEntity<>("Password is too short",HttpStatus.NOT_ACCEPTABLE);

        User user = new User(login, name,passwordEncoder.encode(password));
        int expiration = exp == null ? 30 : Integer.parseInt(exp);
        if (expiration  <  0 || expiration > 365)
            expiration = 30;
        user.setExpiredAt(LocalDate.now().plusDays(expiration));
        userRepository.save(user);

        return new ResponseEntity<>("User is saved", HttpStatus.OK);
    }
    public ResponseEntity<String> deleteUser(int id){
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(null));
        if (user.isAdmin())
            return new ResponseEntity<>("Operation cannot be done",HttpStatus.UNAUTHORIZED);
        userRepository.delete(user);

        return ResponseEntity.ok("User was deleted");
    }
    User findUserByLogin(String login) throws UsernameNotFoundException{
        return userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User with such login is not found"));
    }
    public ResponseEntity<String> editUser(int id, String login, String name){
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(null));

        if (name != null)
            user.setName(name);
        if (login != null){
            if(userRepository.findByLogin(login).isPresent())
                return new ResponseEntity<>("Such login is already occupied",HttpStatus.CONFLICT);
            user.setLogin(login);
        }

        userRepository.save(user);
        return new ResponseEntity<>("User info was updated",HttpStatus.OK);
    }
    public ResponseEntity<String> extendUserDays(int id, int days){
        Optional<User> optional = userRepository.findById(id);

        if (optional.isEmpty() || optional.get().isAdmin())
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        if (days < 0 || days > 365)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        User user = optional.get();
        user.setExpiredAt(user.getExpiredAt().plusDays(days));

        userRepository.save(user);
        return ResponseEntity.ok("This user`s expiration was extended");
    }

    public ResponseEntity<String> changePassword(String token, String password, String newPassword) {
        String login = jwtService.extractLogin(token);
        User user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password,user.getPassword()))
            return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
        if (newPassword.length() < 8)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok("Password successfully changed");
    }

    public Map<String,String> resetPassword(int id, int limit) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(null));
        //TODO Can admin change password and reset?
        Random rd = new Random();
        char[] arr = new char[limit];
        String password;
        for(int i = 0; i < limit; i++){
            arr[i] = alphabet.charAt(rd.nextInt(256)% alphabet.length());
        }
        password = new String(arr);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return Map.of("login",user.getLogin(),"password",password);

    }
}

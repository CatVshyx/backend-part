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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
            return new ResponseEntity<>("Password is too short",HttpStatus.UNPROCESSABLE_ENTITY);

        User user = new User(login, name,passwordEncoder.encode(password));
        int expiration = exp == null ? 30 : Integer.parseInt(exp);
        user.setExpiredAt(LocalDate.now().plusDays(expiration));
        userRepository.save(user);

        return new ResponseEntity<>("User is saved", HttpStatus.OK);
    }
    public boolean deleteUser(int id){
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() || optionalUser.get().isAdmin())
            return false;
        userRepository.delete(optionalUser.get());

        return true;
    }
    User findUserByLogin(String login) throws UsernameNotFoundException{
        return userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User with such login is not found"));
    }
    public ResponseEntity<String> editUser(int id, String login, String name){
        Optional<User> optional = userRepository.findById(id);
        if(optional.isEmpty())
            return new ResponseEntity<>("User is not found", HttpStatus.NOT_FOUND);

        User user = optional.get();
        if (login != null && userRepository.findByLogin(login).isEmpty()){
            user.setLogin(login);
        }
        else{
            return new ResponseEntity<>("Such login is already occupied",HttpStatus.CONFLICT);
        }
        if (name != null){
            user.setName(name);
        }
        userRepository.save(user);

        return new ResponseEntity<>("User info was updated",HttpStatus.OK);
    }
    public ResponseEntity<String> extendUserDays(int id, int days){
//        Optional<User> optional = userRepository.findById(id);
//
//        if (optional.isEmpty())
//            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
//        if (days < 0 || days > 365)
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//
//        User user = optional.get();
//        user.setExpiredAt(user.getExpiredAt().plusDays(days));

        if (days < 0 || days > 365)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Optional<User> optional = userRepository.findById(id);
        optional.or
        optional.ifPresentOrElse(
                (user) -> {
                    user.setExpiredAt(user.getExpiredAt().plusDays(days));
                    userRepository.save(user);
                    },
                () -> {throw new UsernameNotFoundException("Username not found");}
        );

        return ResponseEntity.ok("This user`s expiration was extended");
    }

    public ResponseEntity<String> changePassword(String token, HashMap<String, String> map) {
        String login = jwtService.extractLogin(token);
        Optional<User> optional = userRepository.findByLogin(login);
        optional.ifPresentOrElse((user) -> {}, () -> {});
        return null;
    }
}

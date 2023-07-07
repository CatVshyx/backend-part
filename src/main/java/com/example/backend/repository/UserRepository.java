package com.example.backend.repository;

import com.example.backend.dao.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Integer> {

    Optional<User> findByLogin(String login);
}

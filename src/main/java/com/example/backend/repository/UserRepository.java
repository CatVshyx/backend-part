package com.example.backend.repository;

import com.example.backend.dao.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User,Integer> {

    Optional<User> findByLogin(String login);

    boolean existsByName(String name);

    boolean existsByLogin(String login);
}

package com.example.backend.repository;

import com.example.backend.dao.User;
import org.springframework.data.repository.CrudRepository;
public interface UserRepository extends CrudRepository<User,Integer> {
}

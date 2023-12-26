package com.example.backend.repository;

import com.example.backend.dao.Form;
import org.springframework.data.repository.ListCrudRepository;

public interface FormRepository extends ListCrudRepository<Form, Long> {
    boolean existsByName(String name);

}

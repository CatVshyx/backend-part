package com.example.backend.repository;

import com.example.backend.dao.Category;
import org.springframework.data.repository.ListCrudRepository;

public interface CategoryRepository extends ListCrudRepository<Category,Integer> {
}

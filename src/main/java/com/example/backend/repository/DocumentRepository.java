package com.example.backend.repository;

import com.example.backend.dao.Document;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import java.util.List;

public interface DocumentRepository extends ListCrudRepository<Document,Long> {

    @Query(value = "SELECT * FROM document docs WHERE docs.type_id = ?1",nativeQuery = true)
    List<Document> findAllByTypeId(int id);

    boolean existsByName(String name);
}

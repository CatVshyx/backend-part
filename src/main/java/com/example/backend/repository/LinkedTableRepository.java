package com.example.backend.repository;

import com.example.backend.dao.LinkedTable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface LinkedTableRepository extends CrudRepository<LinkedTable,Long> {
    @Query(value = "SELECT * FROM linked_table t WHERE t.category_id = ?1", nativeQuery = true)
    List<LinkedTable> findAllByCategory(int id);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM linked_table WHERE linked_table.form_id = ?1", nativeQuery = true)
    void deleteByFormId(long id);

    @Query(value = "SELECT  category_id , list_number FROM linked_table t WHERE t.form_id = ?1 ", nativeQuery = true)
    List<Object[]> findCategoriesAndPositionsByFormId(Long i);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM linked_table lt WHERE lt.form_id = :id AND lt.category_id NOT IN :values", nativeQuery = true)
    void deleteCategoriesById(@Param("id") Long id, @Param("values") List<Integer> array);


}

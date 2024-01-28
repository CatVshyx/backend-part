package com.example.backend.repository;

import com.example.backend.dao.VideoInfo;
import org.springframework.data.repository.ListCrudRepository;


public interface VideoRepository extends ListCrudRepository<VideoInfo,Long> {
    boolean existsByTitle(String title);

    boolean existsByUrl(String url);
}

package com.example.backend.service;

import com.example.backend.dao.VideoInfo;
import com.example.backend.repository.VideoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class VideoService {
    @Autowired
    private VideoRepository videoRepository;


    public List<VideoInfo> getVideos(){
        return videoRepository.findAll();
    }
    public VideoInfo createVideoInfo(String title, String description, String url){
        if(videoRepository.existsByUrl(url))
            throw new EntityExistsException("Same url can`t be defined again");
        VideoInfo info = new VideoInfo(title,description,url);
        return videoRepository.save(info);
    }
    public void editVideoInfo(Long id,String title, String description, String url){
        VideoInfo info = videoRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (videoRepository.existsByTitle(title) && !info.getTitle().equals(title))
            throw new EntityExistsException("Such name is already defined");
        if (description != null)
            info.setDescription(description);
        if (url != null)
            info.setUrl(url);

        videoRepository.save(info);
    }
    public void deleteVideoInfo(long id){
        videoRepository.deleteById(id);
    }
}

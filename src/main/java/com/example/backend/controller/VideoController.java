package com.example.backend.controller;

import com.example.backend.dao.VideoInfo;
import com.example.backend.service.VideoService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;
    @GetMapping(path = {"","/"})
    public ResponseEntity<Object> getVideos(){
        return ResponseEntity.ok(videoService.getVideos());
    }
    @PostMapping("/create")
    public ResponseEntity<Object> createVideo(@RequestBody  Map<String,String> map){
        String title = map.get("title");
        String desc = map.get("description");
        String url = map.get("url");

        if (title == null || url == null)
            throw  new NullPointerException();
        VideoInfo info = videoService.createVideoInfo(title,desc,url);
        return ResponseEntity.ok(info);
    }
    @PatchMapping("/edit")
    public ResponseEntity<Object> editVideo(@RequestBody Map<String,String> map) throws NumberFormatException{
        Long id = Long.parseLong(map.get("id"));
        videoService.editVideoInfo(id,map.get("title"),map.get("description"),map.get("url"));
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteVideo(@PathParam(value = "id") long id){
        videoService.deleteVideoInfo(id);
        return ResponseEntity.ok().build();
    }
}

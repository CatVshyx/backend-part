package com.example.backend.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class VideoInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public VideoInfo() {
    }
    public VideoInfo(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }
    private String title;

    private String description;
    private String url;

    public long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

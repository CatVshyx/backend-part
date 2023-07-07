package com.example.backend.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String formLink;
    private String sampleLink;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormLink() {
        return formLink;
    }

    public void setFormLink(String formLink) {
        this.formLink = formLink;
    }

    public String getSampleLink() {
        return sampleLink;
    }

    public void setSampleLink(String sampleLink) {
        this.sampleLink = sampleLink;
    }
}

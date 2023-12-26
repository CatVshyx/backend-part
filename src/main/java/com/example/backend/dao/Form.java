package com.example.backend.dao;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String blankLink;
    private String sampleLink;

    @OneToMany( mappedBy = "form")
    private List<LinkedTable> linkedTable;
    public Form() {}

    public Form(String name) {
        this.name = name;
    }

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

    public String getBlankLink() {
        return blankLink;
    }

    public void setBlankLink(String blankLink) {
        this.blankLink = blankLink;
    }

    public String getSampleLink() {
        return sampleLink;
    }

    public void setSampleLink(String sampleLink) {
        this.sampleLink = sampleLink;
    }

    @Override
    public String toString() {
        return "Form{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", blankLink='" + blankLink + '\'' +
                ", sampleLink='" + sampleLink + '\'' +
                '}';
    }
}

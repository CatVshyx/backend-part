package com.example.backend.dao;

import jakarta.persistence.*;

@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @ManyToOne(targetEntity = Type.class)
    @JoinColumn(name = "type_id")
    private Type type;
    private String link;

    private String description;
    public Document() {}

    public Document(String name, Type type, String link,String description) {
        this.name = name;
        this.type = type;
        this.link = link;
        this.description = description;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

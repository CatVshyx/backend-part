package com.example.backend.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
@EntityScan
public class LinkedTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Category.class)
    @JsonIgnore
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(targetEntity = Form.class)
    @JoinColumn(name = "form_id")
    private Form form;

    private int listNumber;

    public LinkedTable() {}

    public LinkedTable(Category category, Form form, int listNumber) {
        this.category = category;
        this.form = form;
        this.listNumber = listNumber;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public long getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

    @Override
    public String toString() {
        return "LinkedTable{" +
                "id=" + id +
                ", category=" + category +
                ", document=" + form +
                ", listNumber=" + listNumber +
                '}';
    }
}

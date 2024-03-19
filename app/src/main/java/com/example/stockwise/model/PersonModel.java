package com.example.stockwise.model;

import java.io.Serializable;

public class PersonModel implements Serializable {
    private String id;
    private String name;
    private String contact_num;
    private String gender;

    // constructor
    public PersonModel() {}

    public PersonModel(String name, String contact_num,String gender) {
        this.name = name;
        this.contact_num = contact_num;
        this.gender = gender;
    }

    // setter
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact_num(String contact_num) {
        this.contact_num = contact_num;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact_num() {
        return contact_num;
    }

    public String getGender() {
        return gender;
    }
}
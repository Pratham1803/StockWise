package com.example.stockwise.model;

public class PersonModel {
    private String id;
    private String name;
    private String contact_num;

    // constructor
    public PersonModel() {}

    public PersonModel(String name, String contact_num) {
        this.name = name;
        this.contact_num = contact_num;
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
}
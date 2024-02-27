package com.example.stockwise.model;

import java.util.ArrayList;

public class TransactionModel {
    private String id;
    private String person;
    private String date;
    private ArrayList<String> ITEM_LIST;

    // constructor
    public TransactionModel() {}

    public TransactionModel(String person, String date) {
        this.person = person;
        this.date = date;
    }

    // setters
    public void setId(String id) {
        this.id = id;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setITEM_LIST(ArrayList<String> ITEM_LIST) {
        this.ITEM_LIST = ITEM_LIST;
    }

    // getters

    public String getId() {
        return id;
    }

    public String getPerson() {
        return person;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getITEM_LIST() {
        return ITEM_LIST;
    }
}
package com.example.stockwise.model;

import java.util.ArrayList;

public class OwnerModel {
    private String id;
    private String shop_name;
    private String owner_name;
    private String email_id;
    private String contact_num;
    private String fcm_token;
    private String picture;
    private ArrayList<String> LS_CATEGORY;
    private ArrayList<String> LS_ITEM;
    private ArrayList<String> LS_PERSON;
    private ArrayList<String> LS_TRANSACTION;

    // constructor
    public OwnerModel(){}

    public OwnerModel(String shop_name,String owner_name, String contact_num, String fcm_token, String picture,String email_id) {
        this.shop_name = shop_name;
        this.email_id = email_id;
        this.owner_name = owner_name;
        this.contact_num = contact_num;
        this.fcm_token = fcm_token;
        this.picture = picture;
    }

    // setters
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public void setContact_num(String contact_num) {
        this.contact_num = contact_num;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setLS_CATEGORY(ArrayList<String> LS_CATEGORY) {
        this.LS_CATEGORY = LS_CATEGORY;
    }

    public void setLS_ITEM(ArrayList<String> LS_ITEM) {
        this.LS_ITEM = LS_ITEM;
    }

    public void setLS_PERSON(ArrayList<String> LS_PERSON) {
        this.LS_PERSON = LS_PERSON;
    }

    public void setLS_TRANSACTION(ArrayList<String> LS_TRANSACTION) {
        this.LS_TRANSACTION = LS_TRANSACTION;
    }

    // getter
    public String getId() {
        return id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getContact_num() {
        return contact_num;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public String getPicture() {
        return picture;
    }

    public ArrayList<String> getLS_CATEGORY() {
        return LS_CATEGORY;
    }

    public ArrayList<String> getLS_ITEM() {
        return LS_ITEM;
    }

    public ArrayList<String> getLS_PERSON() {
        return LS_PERSON;
    }

    public ArrayList<String> getLS_TRANSACTION() {
        return LS_TRANSACTION;
    }
}
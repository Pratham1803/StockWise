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
    private ArrayList<ProductModel> arrAllProduct;
    private ArrayList<ProductModel> arrUnAvailableProduct;
    private ArrayList<ProductModel> arrAtReorderPointProduct;
    private ArrayList<DbTransactionModel> arrTransactions;
    private ArrayList<CategoryModel> arrCategory;

    public ArrayList<DbTransactionModel> getArrTransactions() {
        return arrTransactions;
    }

    public void setArrTransactions(ArrayList<DbTransactionModel> arrTransactions) {
        this.arrTransactions = arrTransactions;
    }

    public ArrayList<CategoryModel> getArrCategory() {
        return arrCategory;
    }

    public void setArrCategory(ArrayList<CategoryModel> arrCategory) {
        this.arrCategory = arrCategory;
    }

    public ArrayList<ProductModel> getArrAllProduct() {
        return arrAllProduct;
    }

    public void setArrAllProduct(ArrayList<ProductModel> arrAllProduct) {
        this.arrAllProduct = arrAllProduct;
    }

    public ArrayList<ProductModel> getArrUnAvailableProduct() {
        return arrUnAvailableProduct;
    }

    public void setArrUnAvailableProduct(ArrayList<ProductModel> arrUnAvailableProduct) {
        this.arrUnAvailableProduct = arrUnAvailableProduct;
    }

    public ArrayList<ProductModel> getArrAtReorderPointProduct() {
        return arrAtReorderPointProduct;
    }

    public void setArrAtReorderPointProduct(ArrayList<ProductModel> arrAtReorderPointProduct) {
        this.arrAtReorderPointProduct = arrAtReorderPointProduct;
    }

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

}
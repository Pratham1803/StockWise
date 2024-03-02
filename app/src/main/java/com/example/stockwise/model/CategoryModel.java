package com.example.stockwise.model;

import java.util.ArrayList;

public class CategoryModel {
    private String id;
    private String name;
    private String numOfProducts;
    private ArrayList<String> arrProducts;

    public CategoryModel() {
    }

    public CategoryModel(String id, String name, String numOfProducts) {
        this.id = id;
        this.name = name;
        this.numOfProducts = numOfProducts;
    }

    // setters

    public void setArrProducts(ArrayList<String> arrProducts) {
        this.arrProducts = arrProducts;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setNumOfProducts(String numOfProducts) {
        this.numOfProducts = numOfProducts;
    }

    // GETTERS
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getNumOfProducts() {
        return numOfProducts;
    }

    public ArrayList<String> getArrProducts() {
        return arrProducts;
    }
}
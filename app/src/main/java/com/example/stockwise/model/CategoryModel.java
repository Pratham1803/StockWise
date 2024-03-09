package com.example.stockwise.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryModel {
    private String id;
    private String name;
    private String numOfProducts;
    private Map<String,String> arrProducts;

    public CategoryModel() {
    }

    public CategoryModel(String id, String name, String numOfProducts,Map<String,String> arrProducts) {
        this.id = id;
        this.name = name;
        this.numOfProducts = numOfProducts;
        this.arrProducts = arrProducts;
    }

    // setters

    public void setArrProducts(Map<String,String> arrProducts) {
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

    public Map<String,String> getArrProducts() {
        return arrProducts;
    }
}
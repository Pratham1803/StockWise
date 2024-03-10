package com.example.stockwise.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryModel {
    private String id;
    private String name;
    private List<String> arrProducts;

    public CategoryModel() {
    }
    public CategoryModel(String id, String name,List<String> arrProducts) {
        this.id = id;
        this.name = name;
        this.arrProducts = arrProducts;
    }

    // setters

    public void setArrProducts(List<String> arrProducts) {
        this.arrProducts = arrProducts;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }


    // GETTERS
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public List<String> getArrProducts() {
        return arrProducts;
    }
}
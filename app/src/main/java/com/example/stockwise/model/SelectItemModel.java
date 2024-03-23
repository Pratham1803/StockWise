package com.example.stockwise.model;

public class SelectItemModel {
    private String id;
    private String name;
    private String quantity;

    public SelectItemModel(String id, String name, String quantity,String price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

}

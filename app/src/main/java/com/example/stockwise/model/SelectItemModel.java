package com.example.stockwise.model;

public class SelectItemModel {
    private String id;
    private String name;
    private String quantity;
    private String price;

    public SelectItemModel(){}

    public SelectItemModel(String id, String name, String quantity,String price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
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

    public String getPrice() {
        return price;
    }
}

package com.example.stockwise.model;

public class SelectItemModel {
    private final String id;
    private final String name;
    private final String quantity;
    private final String price;

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

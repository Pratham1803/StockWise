package com.example.stockwise.model;

import java.io.Serializable;

public class SelectItemModel implements Serializable {
    private String id;
    private String name;
    private String quantity;
    private String sale_price;
    private String purchase_price;

    public SelectItemModel(){}

    public SelectItemModel(String id, String name, String quantity,String sale_price, String purchase_price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.sale_price = sale_price;
        this.purchase_price = purchase_price;
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

    public String getSale_price() {
        return sale_price;
    }

    public String getPurchase_price() {
        return purchase_price;
    }
}

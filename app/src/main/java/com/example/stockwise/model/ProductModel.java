package com.example.stockwise.model;

public class ProductModel {
    private String id;
    private String name;
    private String barCodeNum;
    private String current_stock;
    private String reorder_point;
    private String purchase_price;
    private String category;
    private String sale_price;
    private String picture;

    // constructor

    public ProductModel() {}

    public ProductModel(String name,String barCodeNum, String current_stock, String reorder_point, String purchase_price, String category, String sale_price, String picture) {
        this.name = name;
        this.barCodeNum = barCodeNum;
        this.current_stock = current_stock;
        this.reorder_point = reorder_point;
        this.purchase_price = purchase_price;
        this.category = category;
        this.sale_price = sale_price;
        this.picture = picture;
    }

    // setters

    public void setBarCodeNum(String barCodeNum) {
        this.barCodeNum = barCodeNum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrent_stock(String current_stock) {
        this.current_stock = current_stock;
    }

    public void setReorder_point(String reorder_point) {
        this.reorder_point = reorder_point;
    }

    public void setPurchase_price(String purchase_price) {
        this.purchase_price = purchase_price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }


    // getters

    public String getBarCodeNum() {
        return barCodeNum;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrent_stock() {
        return current_stock;
    }

    public String getReorder_point() {
        return reorder_point;
    }

    public String getPurchase_price() {
        return purchase_price;
    }

    public String getCategory() {
        return category;
    }

    public String getSale_price() {
        return sale_price;
    }

    public String getPicture() {
        return picture;
    }
}
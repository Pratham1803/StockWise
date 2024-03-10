package com.example.stockwise.model;
import  java.io.Serializable;
public class ProductModel implements Serializable {
    private String id;
    private String name;
    private String current_stock;
    private String reorder_point;
    private String purchase_price;
    private String category;
    private String category_id;
    private String sale_price;
    private String picture;
    private String isReorderPointReached;
    private String isOutOfStock;

    // constructor
    public ProductModel() {}

    public ProductModel(String name, String current_stock, String reorder_point, String purchase_price, String category, String sale_price, String picture, String isReorderPointReached, String isOutOfStock) {
        this.name = name;
        this.current_stock = current_stock;
        this.reorder_point = reorder_point;
        this.purchase_price = purchase_price;
        this.category = category;
        this.sale_price = sale_price;
        this.picture = picture;
        this.isReorderPointReached = isReorderPointReached;
        this.isOutOfStock = isOutOfStock;
    }

    // setters

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }


    public void setIsReorderPointReached(String isReorderPointReached) {
        this.isReorderPointReached = isReorderPointReached;
    }

    public void setIsOutOfStock(String isOutOfStock) {
        this.isOutOfStock = isOutOfStock;
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

    public String getCategory_id() {
        return category_id;
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

    public String getIsReorderPointReached() {
        return isReorderPointReached;
    }

    public String getIsOutOfStock() {
        return isOutOfStock;
    }
}
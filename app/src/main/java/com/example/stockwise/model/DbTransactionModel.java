package com.example.stockwise.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DbTransactionModel implements Serializable {
    private String id;
    private String  person_id;
    private String date;
    private List<SelectItemModel> item_LIST;
    private String note;
    private String total_price;
    private String isPurchase;

    public DbTransactionModel(){
        item_LIST = new ArrayList<>();
    }

    public DbTransactionModel(String id, String person_id, String date, List<SelectItemModel> item_LIST, String total_price) {
        this.id = id;
        this.person_id = person_id;
        this.date = date;
        this.item_LIST = item_LIST;
        this.total_price = total_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public String getIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(String isPurchase) {
        this.isPurchase = isPurchase;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<SelectItemModel> getITEM_LIST() {
        return item_LIST;
    }

    public void setITEM_LIST(List<SelectItemModel> ITEM_LIST) {
        this.item_LIST = ITEM_LIST;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
package com.example.stockwise.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DbTransactionModel implements Serializable {
    private String id;
    private String  person_id;
    private String date;
    private List<SelectItemModel> ITEM_LIST;
    private String note;

    public DbTransactionModel(){
        ITEM_LIST = new ArrayList<>();
    }

    public DbTransactionModel(String id, String person_id, String date, List<SelectItemModel> ITEM_LIST, String note) {
        this.id = id;
        this.person_id = person_id;
        this.date = date;
        this.ITEM_LIST = ITEM_LIST;
        this.note = note;
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
        return ITEM_LIST;
    }

    public void setITEM_LIST(List<SelectItemModel> ITEM_LIST) {
        this.ITEM_LIST = ITEM_LIST;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
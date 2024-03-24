package com.example.stockwise.model;

import java.io.Serializable;
import java.util.List;

public class TransactionModel implements Serializable {
    private PersonModel person;
    private String date;
    private List<ProductModel> ITEM_LIST;
    private String note;
    private DbTransactionModel dbTransactionModel;
    private boolean isPurchase;

    // constructor
    public TransactionModel() {}
    public TransactionModel(PersonModel person, String date, List<ProductModel> ITEM_LIST) {
        this.person = person;
        this.date = date;
        this.ITEM_LIST = ITEM_LIST;
    }

    // getters

    public boolean isPurchase() {
        return isPurchase;
    }

    public void setPurchase(boolean purchase) {
        isPurchase = purchase;
    }

    public DbTransactionModel getDbTransactionModel() {
        return dbTransactionModel;
    }

    public void setDbTransactionModel(DbTransactionModel dbTransactionModel) {
        this.dbTransactionModel = dbTransactionModel;
    }

    public PersonModel getPerson() {
        return person;
    }

    public String getDate() {
        return date;
    }

    public List<ProductModel> getITEM_LIST() {
        return ITEM_LIST;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // setter

    public void setPerson(PersonModel person) {
        this.person = person;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setITEM_LIST(List<ProductModel> ITEM_LIST) {
        this.ITEM_LIST = ITEM_LIST;
    }
}
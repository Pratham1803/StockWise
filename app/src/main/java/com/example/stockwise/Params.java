package com.example.stockwise;

import android.util.Log;

import com.example.stockwise.model.OwnerModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Params {
    private static OwnerModel OWNER_MODEL;
    private static String ID;
    private static String NAME;
    private static String SHOP_NAME;
    private static String ARR_PRODUCTS;
    private static String OWNER_NAME;
    private static String BAR_CODE;
    private static String EMAIL_ID;
    private static String CONTACT_NUMBER;
    private static String FCM_TOKEN;
    private static String PROFILE_PIC;
    private static String CATEGORY;
    private static String PRODUCT;
    private static String CURRENT_STOCK;
    private static String REORDER_POINT;
    private static String PURCHASE_PRICE;
    private static String SALE_PRICE;
    private static String PERSON;
    private static String VENDOR;
    private static String CUSTOMER;
    private static String TRANSACTION;
    private static String DATE;
    private static String GST_NUM;
    private static String CIN_NUM;
    private static String ADDRESS;
    private static FirebaseDatabase DATABASE;
    private static DatabaseReference REFERENCE;
    private static FirebaseAuth AUTH;
    private static StorageReference STORAGE;

    public Params() {
        ID = "id";
        NAME = "name";
        SHOP_NAME = "shop_name";
        OWNER_NAME = "owner_name";
        EMAIL_ID = "email_id";
        CONTACT_NUMBER = "contact_num";
        FCM_TOKEN = "fcm_token";
        PROFILE_PIC = "picture";
        CURRENT_STOCK = "current_stock";
        REORDER_POINT = "reorder_point";
        PURCHASE_PRICE = "purchase_price";
        CATEGORY = "category";
        SALE_PRICE = "sale_price";
        PRODUCT = "product";
        PERSON = "person";
        VENDOR = "vendor";
        CUSTOMER = "customer";
        DATE = "date";
        ARR_PRODUCTS = "arrProducts";
        TRANSACTION = "transaction";
        BAR_CODE = "barCodeNum";
        GST_NUM = "gst_num";
        CIN_NUM = "cin_num";
        ADDRESS = "address";

        OWNER_MODEL = new OwnerModel();
        // database models
        DATABASE = FirebaseDatabase.getInstance();
        AUTH = FirebaseAuth.getInstance();
        STORAGE = FirebaseStorage.getInstance().getReference();
    }

    // setters
    public static void setFcmToken(String fcmToken) {
        FCM_TOKEN = fcmToken;
    }

    public static void setREFERENCE() {
        try{
            Params.REFERENCE = DATABASE.getReference(OWNER_MODEL.getId());
        }catch (Exception e){
            Log.d("ErrorMsg", "setREFERENCE: "+e.toString());
        }
    }
    public static void setOwnerModel(OwnerModel ownerModel) {
        Params.OWNER_MODEL = ownerModel;
    }

    // getters

    public static String getBarCode() {
        return BAR_CODE;
    }

    public static String getGstNum() {
        return GST_NUM;
    }

    public static String getCinNum() {
        return CIN_NUM;
    }

    public static String getADDRESS() {
        return ADDRESS;
    }

    public static String getNAME() {
        return NAME;
    }

    public static OwnerModel getOwnerModel() {
        return OWNER_MODEL;
    }

    public static String getID() {
        return ID;
    }

    public static String getEmailId() {
        return EMAIL_ID;
    }

    public static String getShopName() {
        return SHOP_NAME;
    }

    public static String getOwnerName() {
        return OWNER_NAME;
    }

    public static String getContactNumber() {
        return CONTACT_NUMBER;
    }

    public static String getFcmToken() {
        return FCM_TOKEN;
    }

    public static String getProfilePic() {
        return PROFILE_PIC;
    }

    public static String getCATEGORY() {
        return CATEGORY;
    }

    public static String getPRODUCT() {
        return PRODUCT;
    }

    public static String getCurrentStock() {
        return CURRENT_STOCK;
    }

    public static String getReorderPoint() {
        return REORDER_POINT;
    }

    public static String getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    public static String getSalePrice() {
        return SALE_PRICE;
    }

    public static String getPERSON() {
        return PERSON;
    }

    public static String getVENDOR() {
        return VENDOR;
    }

    public static String getCUSTOMER() {
        return CUSTOMER;
    }

    public static String getTRANSACTION() {
        return TRANSACTION;
    }

    public static String getDATE() {
        return DATE;
    }

    public static String getArrProducts() {
        return ARR_PRODUCTS;
    }

    public static FirebaseDatabase getDATABASE() {
        return DATABASE;
    }

    public static DatabaseReference getREFERENCE() {
        return REFERENCE;
    }

    public static FirebaseAuth getAUTH() {
        return AUTH;
    }

    public static StorageReference getSTORAGE() {
        return STORAGE;
    }
}
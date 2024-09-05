package com.example.debudgger;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tblSavings")
public class SavingsClass {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int uid;

    @ColumnInfo(name = "itemname")
    String itemName;

    @ColumnInfo(name = "date_bought")
    String date;

    @ColumnInfo(name = "price")
    double price;

    @ColumnInfo(name = "item_image")
    byte[] image;

    public SavingsClass(String itemName, String date, double price, byte[] image) {
        this.itemName = itemName;
        this.date = date;
        this.price = price;
        this.image = image;
    }
}

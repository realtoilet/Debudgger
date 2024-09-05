package com.example.debudgger;
import androidx.lifecycle.LiveData;
import androidx.room.*;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavingsListener {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(SavingsClass s);

    @Query("SELECT * FROM tblSavings ORDER BY date_bought")
    LiveData<List<SavingsClass>> getAllData();

    @Query("SELECT * FROM tblSavings ORDER BY date_bought")
    List<SavingsClass> getAllDataAsList();

    @Query("SELECT * FROM tblSavings WHERE date_bought = :date")
    List<SavingsClass> sortedDataWhenBought(String date);

    @Query("SELECT * FROM tblSavings WHERE date_bought > :date")
    List<SavingsClass> sortedDataBeforeBought(String date);

    @Query("SELECT * FROM tblSavings WHERE itemname = :name")
    List<SavingsClass>sortedDataByName(String name);

    @Query("UPDATE tblSavings SET itemname = :name, price = :price WHERE uid = :uid")
    void updateThings(String name, double price, int uid);

    @Query("DELETE FROM tblSavings WHERE uid = :uid")
    void deleteItem(int uid);
}

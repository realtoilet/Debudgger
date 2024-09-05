package com.example.debudgger;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SavingsClass.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context c){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(c.getApplicationContext(), AppDatabase.class, "SavingsDB").allowMainThreadQueries().build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract SavingsListener savings();
}

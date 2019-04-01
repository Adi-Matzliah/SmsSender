package com.exercise.continusebiometrics.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class, Event.class}, version = 1, exportSchema = false)
public abstract class ContactsDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
    public abstract EventDao eventDao();
}


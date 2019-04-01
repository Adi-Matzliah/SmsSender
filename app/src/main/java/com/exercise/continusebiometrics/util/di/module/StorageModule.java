package com.exercise.continusebiometrics.util.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.exercise.continusebiometrics.data.ContactDao;
import com.exercise.continusebiometrics.data.ContactsDatabase;
import com.exercise.continusebiometrics.data.EventDao;
import com.exercise.continusebiometrics.util.di.scope.AppScope;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import dagger.Module;
import dagger.Provides;


@Module
public class StorageModule {

    @NonNull
    private String fileName;

    @NonNull
    private RoomDatabase roomDB;

    public StorageModule(Context context, String fileName) {
        this.fileName = fileName;
        this.roomDB = Room.databaseBuilder(context, ContactsDatabase.class, fileName).build();
    }

    @Provides
    @AppScope
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @AppScope
    synchronized ContactsDatabase provideDB(Context context) {
        return Room.databaseBuilder(context,
                ContactsDatabase.class, fileName).build();
    }

    @Provides
    @AppScope
    ContactDao provideContactsDao(ContactsDatabase db) {
        return db.contactDao();
    }

    @Provides
    @AppScope
    EventDao provideEventsDao(ContactsDatabase db) {
        return db.eventDao();
    }

}

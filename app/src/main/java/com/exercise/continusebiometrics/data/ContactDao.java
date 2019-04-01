package com.exercise.continusebiometrics.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    Flowable<List<Contact>> getAll();

    @Query("SELECT * FROM contact WHERE first_name LIKE :first AND "
           + "last_name LIKE :last LIMIT 1")
    Contact findByName(String first, String last);

    @Insert(onConflict = REPLACE)
    void insertOrUpdateAll(Contact... contacts);

    @Query("DELETE FROM contact")
    void deleteAll();
}
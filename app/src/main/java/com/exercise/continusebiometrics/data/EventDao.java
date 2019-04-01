package com.exercise.continusebiometrics.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import androidx.annotation.StringDef;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.exercise.continusebiometrics.data.EventDao.Order.ASC;
import static com.exercise.continusebiometrics.data.EventDao.Order.DESC;

@Dao
public interface EventDao {
    @StringDef({
            ASC,
            DESC
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Order {
        String ASC = "ASC";
        String DESC = "DESC";
    }

    @Query("select * from event ORDER BY CASE :orderBy WHEN 'name' THEN name WHEN 'date' THEN date WHEN 'status' THEN status END DESC")
    Flowable<List<Event>> getAllOrderBy(String orderBy/*, @Order String order*/);

    @Insert(onConflict = REPLACE)
    void insertOrUpdateAll(Event... events);

    @Query("DELETE FROM event")
    void deleteAll();
}
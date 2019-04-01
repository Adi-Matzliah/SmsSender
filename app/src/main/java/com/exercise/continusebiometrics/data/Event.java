package com.exercise.continusebiometrics.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Timestamp;

import androidx.annotation.StringDef;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.exercise.continusebiometrics.data.Event.Status.FAILURE;
import static com.exercise.continusebiometrics.data.Event.Status.SUCCESS;
import static com.exercise.continusebiometrics.data.Event.Type.CONTACT_SAVED;
import static com.exercise.continusebiometrics.data.Event.Type.SMS;

@Entity
public class Event {

    @StringDef({
            SUCCESS,
            FAILURE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
    }

    @StringDef({
            CONTACT_SAVED,
            SMS
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        String CONTACT_SAVED = "contact_saved";
        String SMS = "SMS";
    }

    public Event(String name, @Status String status, @Type String type) {
        this.name = name;
        this.date = new Timestamp(System.currentTimeMillis()).toString();
        this.status = status;
        this.type = type;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "status")
    private @Status String status;

    @ColumnInfo(name = "type")
    private @Type String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

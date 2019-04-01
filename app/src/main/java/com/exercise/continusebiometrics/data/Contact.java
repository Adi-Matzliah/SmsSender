package com.exercise.continusebiometrics.data;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Contact {

    @NonNull
    @PrimaryKey
    @ColumnInfo (name = "phone")
    private String phone;

    @Nullable
    @ColumnInfo (name = "first_name")
    private String firstName;

    @Nullable
    @ColumnInfo (name = "last_name")
    private String lastName;

    @NonNull
    @ColumnInfo (name = "created_at")
    private String createdAt;

    public Contact() {
        setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
    }

    @Ignore
    public Contact(String phone) {
        this.phone = phone;
        setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
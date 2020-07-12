package com.p.contactsbook.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Contact implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "number")
    private String number;

    @ColumnInfo(name = "location")
    private String location;

    public Contact() {
    }

    @Ignore
    public Contact(String id, String name, String number, String location) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.location = location;
    }

    public boolean equals(Object o){
        Contact e = (Contact) o;
        return this.id.equals(e.getId());
    }
}
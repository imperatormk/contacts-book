package com.p.contactsbook.entities;

import java.io.Serializable;

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

    private String id;
    private String name;
    private String number;
    private String location;

    public Contact() {
    }

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
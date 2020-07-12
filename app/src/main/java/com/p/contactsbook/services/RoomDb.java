package com.p.contactsbook.services;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.entities.ContactDao;

@Database(entities = {Contact.class}, version = 1)
public abstract class RoomDb extends RoomDatabase {
    public abstract ContactDao contactDao();
}

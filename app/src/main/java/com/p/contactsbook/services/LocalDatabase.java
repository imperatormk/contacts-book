package com.p.contactsbook.services;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.entities.ContactDao;
import com.p.contactsbook.entities.ContactViewModel;

import java.util.UUID;

@Database(entities = {Contact.class}, version = 1)
abstract class RoomDb extends RoomDatabase {
    public abstract ContactDao contactDao();
}

public class LocalDatabase {
    private static LocalDatabase INSTANCE = null;
    private RoomDb db;
    private ContactViewModel.ContactListCallback cb;

    private LocalDatabase(Context ctx, ContactViewModel.ContactListCallback cb) {
        this.db = Room.databaseBuilder(ctx, RoomDb.class, "contacts-book").build();
        this.cb = cb;
    }

    public static LocalDatabase getInstance() {
        return INSTANCE;
    }

    public static LocalDatabase getInstance(Context ctx, ContactViewModel.ContactListCallback cb) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDatabase(ctx, cb);
        }
        return INSTANCE;
    }

    public void initContacts() {
        for (Contact c : INSTANCE.db.contactDao().getAll()) {
            System.out.println("INIT ID IS: " + c.getId());
            cb.contactAdded(c);
        }
    }

    public void addContact(Contact c) {
        c.setId(UUID.randomUUID().toString());
        INSTANCE.db.contactDao().insertAll(c);
        cb.contactAdded(c);
    }

    public void modifyContact(Contact c) {
        INSTANCE.db.contactDao().update(c);
        cb.contactModified(c);
    }

    public void deleteContact(Contact c) {
        INSTANCE.db.contactDao().delete(c);
        cb.contactDeleted(c);
    }
}
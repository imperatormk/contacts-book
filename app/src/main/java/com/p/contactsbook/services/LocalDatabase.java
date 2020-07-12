package com.p.contactsbook.services;

import android.content.Context;

import androidx.room.Room;

public class LocalDatabase {
    private static LocalDatabase INSTANCE = null;
    private RoomDb db;

    private LocalDatabase(Context ctx) {
        this.db = Room.databaseBuilder(ctx, RoomDb.class, "contacts-book").build();
    }

    public static RoomDb getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDatabase(ctx);
        }
        return(INSTANCE.db);
    }
}
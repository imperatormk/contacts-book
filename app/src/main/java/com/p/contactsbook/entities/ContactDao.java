package com.p.contactsbook.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT * FROM contact WHERE id IN (:ids)")
    List<Contact> loadAllByIds(String[] ids);

    @Insert
    void insertAll(Contact... contacts);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);
}
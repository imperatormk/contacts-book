package com.p.contactsbook.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT * FROM contact WHERE id IN (:ids)")
    List<Contact> loadAllByIds(String[] ids);

    @Query("SELECT * FROM contact WHERE name LIKE :name LIMIT 1")
    Contact findByName(String name);

    @Insert
    void insertAll(Contact... users);

    @Delete
    void delete(Contact user);
}
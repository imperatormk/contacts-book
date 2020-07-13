package com.p.contactsbook.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

class LocationConverter {
    @TypeConverter
    public static GeoPoint toGeoPoint(String location) {
        try {
            String lat = location.split(" : ")[0];
            String lon = location.split(" : ")[1];

            double latD = Double.valueOf(lat);
            double lonD = Double.valueOf(lon);

            latD = (Math.round(latD * 100.0)) / 100.0;
            lonD = (Math.round(lonD * 100.0)) / 100.0;

            return new GeoPoint(latD, lonD);
        } catch (Exception ignore) {
            return null;
        }
    }

    @TypeConverter
    public static String toLocation(GeoPoint geoPoint) {
        double latD = geoPoint.getLatitude();
        double lonD = geoPoint.getLongitude();

        latD = (Math.round(latD * 100.0)) / 100.0;
        lonD = (Math.round(lonD * 100.0)) / 100.0;

        return latD + " : " + lonD;
    }
}

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

    public GeoPoint getGeoPoint() {
        return LocationConverter.toGeoPoint(location);
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
        this.location = LocationConverter.toLocation(geoPoint);
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "number")
    private String number;

    @ColumnInfo(name = "location")
    private String location;

    @Ignore
    private transient GeoPoint geoPoint;

    public Contact() {
    }

    @Ignore
    public Contact(@NonNull String id, String name, String number, String location) {
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
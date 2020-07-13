package com.p.contactsbook.ui.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import com.p.contactsbook.R;
import com.p.contactsbook.databinding.ActivityManageContactBinding;
import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.entities.ContactViewModel;

public class ManageContactActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private ContactViewModel contactVm;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        contactVm = ViewModelProviders.of(this).get(ContactViewModel.class);
        contactVm.setContact(contact);

        ActivityManageContactBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_contact);
        binding.setContactVm(contactVm);
        binding.setLifecycleOwner(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddContact) {
            Contact c = contactVm.getContact().getValue();

            Intent data = new Intent();
            data.putExtra("contact", c);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void setMapPin(LatLng point){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("pin", "drawable", getPackageName()));
        Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 64, 64, false);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resized);
        MarkerOptions markerOptions = new MarkerOptions().position(point)
                .icon(icon);
        map.clear();
        map.addMarker(markerOptions);
    }

    @Override
    public void onMapClick(LatLng point) {
        System.out.println(point.latitude);

        Contact c = contactVm.mContact.getValue();
        c.setGeoPoint(new GeoPoint(point.latitude, point.longitude));

        contactVm.mContact.postValue(c);
        setMapPin(point);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);

        GeoPoint point = contactVm.mContact.getValue().getGeoPoint();
        if (point != null) {
            setMapPin(new LatLng(point.getLatitude(), point.getLongitude()));
        }
    }
}

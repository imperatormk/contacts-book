package com.p.contactsbook.ui.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.p.contactsbook.R;
import com.p.contactsbook.databinding.ActivityAddContactBinding;
import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.entities.ContactViewModel;

public class ManageContactActivity extends AppCompatActivity implements View.OnClickListener {
    private ContactViewModel contactVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        contactVm = ViewModelProviders.of(this).get(ContactViewModel.class);
        contactVm.setContact(contact);

        ActivityAddContactBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_contact);
        binding.setContactVm(contactVm);
        binding.setLifecycleOwner(this);
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
}

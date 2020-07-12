package com.p.contactsbook.entities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactViewModel extends ViewModel {
    public MutableLiveData<Contact> mContact;

    public ContactViewModel() {
        mContact = new MutableLiveData<>(new Contact());
    }

    public ContactViewModel(Contact c) {
        mContact = new MutableLiveData<>(c);
    }

    public void setContact(Contact c) {
        mContact.setValue(c);
    }

    public LiveData<Contact> getContact() {
        return mContact;
    }
}
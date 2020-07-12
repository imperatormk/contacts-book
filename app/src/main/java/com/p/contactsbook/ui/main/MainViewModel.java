package com.p.contactsbook.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class MainViewModel extends ViewModel {
    private MutableLiveData<FirebaseUser> mUser = new MutableLiveData<>(null);

    public void setUser(FirebaseUser name) {
        mUser.setValue(name);
    }
    public LiveData<FirebaseUser> getUser() {
        return mUser;
    }

    public boolean isLoggedIn() {
        return mUser.getValue() != null;
    }
}
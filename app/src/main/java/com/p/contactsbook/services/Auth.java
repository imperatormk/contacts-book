package com.p.contactsbook.services;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseUser;

public class Auth {
    private AuthViewModel mAuthViewModel;

    public Auth(Fragment f) {
        mAuthViewModel = ViewModelProviders.of(f).get(AuthViewModel.class);
    }

    public AuthViewModel getAuthViewModel() {
        return mAuthViewModel;
    }

    public static class AuthViewModel extends androidx.lifecycle.ViewModel {
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
}

package com.p.contactsbook.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.p.contactsbook.MainActivity;
import com.p.contactsbook.R;
import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.services.Auth;
import com.p.contactsbook.ui.contacts.ManageContactActivity;
import com.p.contactsbook.ui.contacts.ContactFragment;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
    private int RC_SIGN_IN = 777;
    private int RC_CREATE_CONTACT = 778;

    private Auth mAuth;
    private Switch swLocal;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        mAuth = new Auth(this);

        Button btnSignIn = view.findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mAuth.getAuthViewModel().isLoggedIn()) {
                    signOut();
                } else {
                    signIn();
                }
            }
        });

        Button btnAddContact = view.findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchManageContact(new Contact("", "", "", ""));
            }
        });

        swLocal = view.findViewById(R.id.swLocal);
        swLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((ContactFragment) getChildFragmentManager().findFragmentById(R.id.contacts)).initDb(isChecked);
            }
        });

        Button btnLanguage = view.findViewById(R.id.btnLanguage);
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) getActivity()).updateResourcesLegacy(getActivity(), "mk", "MK");
            }
        });

        return view;
    }

    public void launchManageContact(Contact contact) {
        Intent intent = new Intent(getActivity(), ManageContactActivity.class);
        intent.putExtra("contact", contact);
        startActivityForResult(intent, RC_CREATE_CONTACT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth.getAuthViewModel().getUser().observe(requireActivity(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser user) {
                TextView txtWelcome = getView().findViewById(R.id.txtWelcome);
                Button btnLogin = getView().findViewById(R.id.btnSignIn);

                if (user == null) {
                    txtWelcome.setText(getResources().getString(R.string.greeting, ""));
                    btnLogin.setText(R.string.signin);
                } else {
                    txtWelcome.setText(getResources().getString(R.string.greeting, user.getDisplayName()));
                    btnLogin.setText(R.string.signout);
                }

                swLocal.setChecked(user == null);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mAuth.getAuthViewModel().setUser(user);

                boolean isNew = response.isNewUser();
                if (isNew) {
                    // TODO: create new document for user
                    // TODO: transfer local data to cloud
                } else {
                    // TODO: purge local db?
                }
                swLocal.setChecked(false);
            } else {
                System.out.println(response.getError().getMessage());
            }
        } else if (requestCode == RC_CREATE_CONTACT) {
            if (data == null) return;

            Contact c = (Contact) data.getSerializableExtra("contact");
            if (c == null) return;

            ((ContactFragment) getChildFragmentManager().findFragmentById(R.id.contacts)).upsertContact(c);
        }
    }

    private void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            //new AuthUI.IdpConfig.GoogleBuilder().build(),
            //new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.AnonymousBuilder().build());

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mAuth.getAuthViewModel().setUser(null);
    }
}
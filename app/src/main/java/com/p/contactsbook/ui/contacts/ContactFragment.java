package com.p.contactsbook.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;
import com.p.contactsbook.R;
import com.p.contactsbook.entities.ContactViewModel;
import com.p.contactsbook.services.Auth;
import com.p.contactsbook.services.ContactsFirestore;
import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.services.ContactsLocalDatabase;
import com.p.contactsbook.ui.main.MainFragment;

import java.util.ArrayList;
import java.util.UUID;

public class ContactFragment extends Fragment implements OnListFragmentInteractionListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private RecyclerView recyclerView;
    private MyContactRecyclerViewAdapter recycler;

    public ContactFragment() {
    }

    public static ContactFragment newInstance(int columnCount) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    public void initDb() {
        recycler.clearContacts();

        final ContactViewModel.ContactListCallback cb = new ContactViewModel.ContactListCallback() {
            @Override
            public void contactAdded(final Contact contact) {
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run()
                    {
                        recycler.addContact(contact);
                    }
                });
            }

            @Override
            public void contactDeleted(final Contact contact) {
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run()
                    {
                        recycler.deleteContact(contact);
                    }
                });
            }

            @Override
            public void contactModified(final Contact contact) {
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run()
                    {
                        recycler.modifyContact(contact);
                    }
                });
            }
        };

        if (!auth.getAuthViewModel().isLoggedIn()) {
            final ContactsLocalDatabase dbInstance = ContactsLocalDatabase.getInstance(getActivity().getApplicationContext(), cb);
            Toast.makeText(getContext(), "Switched to local mode", Toast.LENGTH_SHORT).show();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    dbInstance.initContacts();
                }
            });
        } else {
            ContactsFirestore.initContacts(auth.getAuthViewModel().getUser().getValue().getUid(), cb);
            Toast.makeText(getContext(), "Switched to cloud mode", Toast.LENGTH_SHORT).show();
        }
    }

    private Auth auth = null;
    public void setAuth(Auth auth) {
        this.auth = auth;
        this.auth.getAuthViewModel().getUser().observe(requireActivity(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser user) {
            initDb();
            }
        });
    }

    private String i;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        recycler = new MyContactRecyclerViewAdapter(new ArrayList<Contact>(), this);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(recycler);
        }

        i = UUID.randomUUID().toString();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        recycler = null;
        recyclerView = null;
    }

    public void upsertContact(final Contact c) {
        boolean isLocal = !auth.getAuthViewModel().isLoggedIn();
        if (c.getId().equals("")) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, UUID.randomUUID().toString());
            bundle.putString("contact_variant", isLocal ? "local" : "cloud");
            FirebaseAnalytics.getInstance(getContext()).logEvent("new_contact", bundle);

            if (isLocal) {
                final ContactsLocalDatabase dbInstance = ContactsLocalDatabase.getInstance();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        dbInstance.addContact(c);
                    }
                });
            } else {
                ContactsFirestore.addContact(auth.getAuthViewModel().getUser().getValue().getUid(), c);
            }
        } else {
            if (isLocal) {
                final ContactsLocalDatabase dbInstance = ContactsLocalDatabase.getInstance();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        dbInstance.modifyContact(c);
                    }
                });
            } else {
                ContactsFirestore.modifyContact(auth.getAuthViewModel().getUser().getValue().getUid(), c);
            }
        }
    }

    @Override
    public void onContactBrowse(Contact contact) {
        MainFragment mainFragment = ((MainFragment) this.getParentFragment());
        mainFragment.launchManageContact(contact, false);
    }

    @Override
    public void onContactEdit(Contact contact) {
        MainFragment mainFragment = ((MainFragment) this.getParentFragment());
        mainFragment.launchManageContact(contact, true);
    }

    @Override
    public void onContactDelete(final Contact contact) {
        if (!auth.getAuthViewModel().isLoggedIn()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                ContactsLocalDatabase dbInstance = ContactsLocalDatabase.getInstance();
                dbInstance.deleteContact(contact);
                }
            });
        } else {
            ContactsFirestore.deleteContact(auth.getAuthViewModel().getUser().getValue().getUid(), contact);
        }
    }

    @Override
    public void onContactCall(Contact contact) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getNumber()));
        startActivity(intent);
    }
}

interface OnListFragmentInteractionListener {
    void onContactBrowse(Contact contact);
    void onContactEdit(Contact contact);
    void onContactDelete(Contact contact);
    void onContactCall(Contact contact);
}
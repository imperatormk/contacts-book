package com.p.contactsbook.ui.contacts;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseUser;
import com.p.contactsbook.R;
import com.p.contactsbook.entities.ContactViewModel;
import com.p.contactsbook.services.Auth;
import com.p.contactsbook.services.ContactsFirestore;
import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.services.ContactsLocalDatabase;
import com.p.contactsbook.ui.main.MainFragment;

import java.util.ArrayList;

public class ContactFragment extends Fragment implements OnListFragmentInteractionListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
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

    Auth auth;
    public void setAuth(Auth auth) {
        this.auth = auth;
        this.auth.getAuthViewModel().getUser().observe(requireActivity(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser user) {
            initDb();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(recycler);
        }

        return view;
    }

    public void upsertContact(final Contact c) {
        if (c.getId().equals("")) {
            if (!auth.getAuthViewModel().isLoggedIn()) {
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
            if (!auth.getAuthViewModel().isLoggedIn()) {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        recycler = new MyContactRecyclerViewAdapter(new ArrayList<Contact>(), this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        recycler = null;
    }

    @Override
    public void onContactEdit(Contact contact) {
        MainFragment mainFragment = ((MainFragment) this.getParentFragment());
        mainFragment.launchManageContact(contact);
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
}

interface OnListFragmentInteractionListener {
    void onContactEdit(Contact contact);
    void onContactDelete(Contact contact);
}
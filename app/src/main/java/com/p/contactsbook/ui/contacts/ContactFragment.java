package com.p.contactsbook.ui.contacts;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.p.contactsbook.R;
import com.p.contactsbook.entities.ContactViewModel;
import com.p.contactsbook.services.Firestore;
import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.services.LocalDatabase;
import com.p.contactsbook.ui.main.MainFragment;

import java.util.ArrayList;

public class ContactFragment extends Fragment implements OnListFragmentInteractionListener {
    private boolean isLocal = false;

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

        initDb(true);
    }

    public void initDb(boolean _isLocal) {
        isLocal = _isLocal;
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

        if (isLocal) {
            final LocalDatabase dbInstance = LocalDatabase.getInstance(getActivity().getApplicationContext(), cb);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    dbInstance.initContacts();
                }
            });
        } else {
            Firestore.initContacts(cb);
        }
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
            if (isLocal) {
                final LocalDatabase dbInstance = LocalDatabase.getInstance();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        dbInstance.addContact(c);
                    }
                });
            } else {
                Firestore.addContact(c);
            }
        } else {
            if (isLocal) {
                final LocalDatabase dbInstance = LocalDatabase.getInstance();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        dbInstance.modifyContact(c);
                    }
                });
            } else {
                Firestore.modifyContact(c);
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
        if (isLocal) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    LocalDatabase dbInstance = LocalDatabase.getInstance();
                    dbInstance.deleteContact(contact);
                }
            });
        } else {
            Firestore.deleteContact(contact);
        }
    }
}

interface OnListFragmentInteractionListener {
    void onContactEdit(Contact contact);
    void onContactDelete(Contact contact);
}
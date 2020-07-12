package com.p.contactsbook.ui.contacts;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.p.contactsbook.R;
import com.p.contactsbook.entities.ContactViewModel;
import com.p.contactsbook.services.Firestore;
import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.services.LocalDatabase;
import com.p.contactsbook.services.RoomDb;
import com.p.contactsbook.ui.main.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements OnListFragmentInteractionListener {
    private boolean isLocal = true;

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

        if (isLocal) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    RoomDb dbInstance = LocalDatabase.getInstance(getActivity().getApplicationContext());
                    List<Contact> contacts = dbInstance.contactDao().getAll();

                    System.out.println(contacts.size());
                }
            });
        } else {
            Firestore.readContacts(new ContactViewModel.ContactListCallback() {
                @Override
                public void contactAdded(Contact contact) {
                    recycler.addContact(contact);
                }

                @Override
                public void contactDeleted(Contact contact) {
                    recycler.deleteContact(contact);
                }

                @Override
                public void contactModified(Contact contact) {
                    recycler.modifyContact(contact);
                }
            });
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
        mainFragment.upsertContact(contact);
    }

    @Override
    public void onContactDelete(Contact contact) {
        Firestore.deleteContact(contact);
    }
}

interface OnListFragmentInteractionListener {
    void onContactEdit(Contact contact);
    void onContactDelete(Contact contact);
}
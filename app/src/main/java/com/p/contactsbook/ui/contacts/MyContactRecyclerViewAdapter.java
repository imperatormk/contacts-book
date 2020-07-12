package com.p.contactsbook.ui.contacts;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.p.contactsbook.R;
import com.p.contactsbook.ui.contacts.ContactFragment.OnListFragmentInteractionListener;
import com.p.contactsbook.entities.Contact;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private final List<Contact> mValues;
    private final OnListFragmentInteractionListener mListener;

    MyContactRecyclerViewAdapter(List<Contact> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Contact contact = mValues.get(position);

        holder.mItem = contact;
        holder.mIdView.setText(contact.getId().substring(0, 5));
        holder.mNameView.setText(contact.getName());
        holder.mNumberView.setText(contact.getNumber());
        holder.mLocationView.setText(contact.getLocation());

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onContactDelete(holder.mItem);
                }
            }
        });
    }

    void updateContacts(List<Contact> contacts) {
        mValues.clear();
        mValues.addAll(contacts);
        notifyDataSetChanged();
    }

    public void addContact(Contact c) {
        mValues.add(c);
        notifyItemInserted(mValues.size() - 1);
    }

    public void deleteContact(Contact c) {
        mValues.remove(c);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mNameView;
        final TextView mNumberView;
        final TextView mLocationView;
        final Button mDeleteBtn;

        Contact mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_id);
            mNameView = view.findViewById(R.id.item_name);
            mNumberView = view.findViewById(R.id.item_number);
            mLocationView = view.findViewById(R.id.item_location);
            mDeleteBtn = view.findViewById(R.id.btnDelete);
        }
    }
}

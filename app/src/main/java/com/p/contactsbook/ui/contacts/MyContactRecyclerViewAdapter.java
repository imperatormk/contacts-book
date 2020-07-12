package com.p.contactsbook.ui.contacts;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.p.contactsbook.R;
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

        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onContactEdit(holder.mItem);
                }
            }
        });
    }

    void addContact(Contact c) {
        mValues.add(c);
        int position = mValues.indexOf(c);
        notifyItemInserted(position);
    }

    void modifyContact(Contact c) {
        int position = mValues.indexOf(c);
        mValues.set(mValues.indexOf(c), c);
        notifyItemChanged(position);
    }

    void deleteContact(Contact c) {
        int position = mValues.indexOf(c);
        mValues.remove(c);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mNameView;
        final TextView mNumberView;
        final TextView mLocationView;
        final Button mDeleteBtn;
        final Button mEditBtn;

        Contact mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.item_name);
            mNumberView = view.findViewById(R.id.item_number);
            mLocationView = view.findViewById(R.id.item_location);
            mDeleteBtn = view.findViewById(R.id.btnDelete);
            mEditBtn = view.findViewById(R.id.btnEdit);
        }
    }
}

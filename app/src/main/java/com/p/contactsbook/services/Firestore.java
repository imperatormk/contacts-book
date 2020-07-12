package com.p.contactsbook.services;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.entities.ContactViewModel;

import java.util.HashMap;
import java.util.Map;

public class Firestore {
    private static FirebaseFirestore getInstance(){
        return FirebaseFirestore.getInstance();
    };

    public static void initContacts(final ContactViewModel.ContactListCallback cb) {
        getInstance().collection("contacts")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot doc = dc.getDocument();
                        Contact contact = doc.toObject(Contact.class);
                        contact.setId(doc.getId());

                        switch (dc.getType()) {
                            case ADDED:
                                cb.contactAdded(contact);
                                break;
                            case MODIFIED:
                                cb.contactModified(contact);
                                break;
                            case REMOVED:
                                cb.contactDeleted(contact);
                                break;
                        }
                    }

                }
            });
    }

    public static void addContact(final Contact contact) {
        Map<String, Object> contactMap = new HashMap<>();
        contactMap.put("name", contact.getName());
        contactMap.put("number", contact.getNumber());
        contactMap.put("location", contact.getLocation());

        getInstance().collection("contacts")
            .add(contactMap);
    }

    public static void modifyContact(final Contact contact) {
        getInstance().collection("contacts").document(contact.getId())
            .set(contact);
    }

    public static void deleteContact(final Contact contact) {
        getInstance().collection("contacts").document(contact.getId())
            .delete();
    }
}
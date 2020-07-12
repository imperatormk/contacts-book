package com.p.contactsbook.services;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.p.contactsbook.entities.Contact;
import com.p.contactsbook.entities.ContactViewModel;

import java.util.HashMap;
import java.util.Map;

public class ContactsFirestore {
    private static FirebaseFirestore getInstance(){
        return FirebaseFirestore.getInstance();
    };

    public static void initContacts(String userId, final ContactViewModel.ContactListCallback cb) {
        getUserContactsCollection(userId)
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

    public static void addContact(String userId, final Contact contact) {
        Map<String, Object> contactMap = new HashMap<>();
        contactMap.put("name", contact.getName());
        contactMap.put("number", contact.getNumber());
        contactMap.put("location", contact.getLocation());

        getUserContactsCollection(userId)
            .add(contactMap);
    }

    public static void modifyContact(String userId, final Contact contact) {
        getUserContactsCollection(userId).document(contact.getId())
            .set(contact);
    }

    public static void deleteContact(String userId, final Contact contact) {
        getUserContactsCollection(userId).document(contact.getId())
            .delete();
    }

    public static void initUser(String userId) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        DocumentReference user = getInstance().collection("users").document(userId);
        user.set(map);
    }

    private static CollectionReference getUserContactsCollection(String userId) {
        return getInstance().collection("users").document(userId).collection("contacts");
    }
}
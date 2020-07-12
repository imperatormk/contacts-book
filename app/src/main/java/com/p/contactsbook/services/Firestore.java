package com.p.contactsbook.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.p.contactsbook.entities.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firestore {
    static FirebaseFirestore getDb(){
        return FirebaseFirestore.getInstance();
    };

    public static void readContacts(final FirestoreCallback cb) {
        getDb().collection("contacts")
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

                        System.out.println("heyaaa " + dc.getType() + " " + contact.getName());

                        switch (dc.getType()) {
                            case ADDED:
                                cb.contactAdded(contact);
                                break;
                            case MODIFIED:
                                System.out.println("Modified contact: " + dc.getDocument().getData());
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

        getDb().collection("contacts")
            .add(contactMap);
    }

    public static void deleteContact(final Contact contact) {
        getDb().collection("contacts").document(contact.getId())
            .delete();
    }

    public interface FirestoreCallback {
        void contactAdded(Contact contact);
        void contactDeleted(Contact contact);
    }
}
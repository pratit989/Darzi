package com.infinityandriod.darzi.Common;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class FirestoreMethods {

    private static final String TAG = "FirestoreMethods";
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void send_data(String collectionName, String documentID, Map<String, Object> data, DocumentReference documentReference) {

        if (documentReference != null) {
            documentReference
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        } else {
            db.collection(collectionName).document(documentID)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
    }

    public void send_data(String collectionName, String documentID, Map<String, Object> data) {
        send_data(collectionName, documentID, data, null);
    }

    public void send_data( Map<String, Object> data, DocumentReference documentReference) {
        send_data(null, null, data, documentReference);
    }
}

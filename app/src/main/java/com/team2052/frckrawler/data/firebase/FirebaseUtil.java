package com.team2052.frckrawler.data.firebase;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Utility class for Firebase this holds commonly used code for Firebase
 */
public class FirebaseUtil {
    private static FirebaseDatabase _firebaseDatabase;

    private FirebaseUtil() {
    }

    /**
     * Use this or the app may crash
     */
    public static FirebaseDatabase getFirebaseDatabase() {
        if (_firebaseDatabase == null) {
            _firebaseDatabase = FirebaseDatabase.getInstance();
            _firebaseDatabase.setPersistenceEnabled(true);
        }
        return _firebaseDatabase;
    }
}

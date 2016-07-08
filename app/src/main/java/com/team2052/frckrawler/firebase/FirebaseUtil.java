package com.team2052.frckrawler.firebase;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {
    private static FirebaseDatabase _firebaseDatabase;

    /**
     * Use this or the app may crash
     */
    public static FirebaseDatabase getFirebaseDatabase(){
        if(_firebaseDatabase == null){
            _firebaseDatabase = FirebaseDatabase.getInstance();
            _firebaseDatabase.setPersistenceEnabled(true);
        }
        return _firebaseDatabase;
    }
}

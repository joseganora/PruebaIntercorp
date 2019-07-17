package ar.prueba.pruebaintercorp;

import com.google.firebase.database.FirebaseDatabase;

public class Database {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDB() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
           // mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}

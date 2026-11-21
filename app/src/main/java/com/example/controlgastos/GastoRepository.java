package com.example.controlgastos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GastoRepository {
    private static final String COLLECTION_NAME = "gastos";
    private final FirebaseFirestore db;

    public GastoRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public CollectionReference getCollection() {
        return db.collection(COLLECTION_NAME);
    }

    public String generarId() {
        return db.collection(COLLECTION_NAME).document().getId();
    }

    public Task<Void> guardarGasto(Gasto gasto) {
        return db.collection(COLLECTION_NAME)
                .document(gasto.getId())
                .set(gasto);
    }

    public Task<Void> eliminarGasto(String id) {
        return db.collection(COLLECTION_NAME)
                .document(id)
                .delete();
    }
}
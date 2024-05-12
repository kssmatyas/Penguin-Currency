package com.example.penguincurrency;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class FirestoreHelper {
    private final FirebaseFirestore firestore;
    private final String userId;

    public FirestoreHelper() {
        this.firestore = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void addCurrency(CurrencyModel currency) {
        Map<String, Object> currencyMap = new HashMap<>();
        currencyMap.put("name", currency.getName());
        currencyMap.put("amount", currency.getAmount());

        firestore.collection("users")
                .document(userId)
                .collection("currencies")
                .add(currencyMap)
                .addOnSuccessListener(documentReference -> {
                    // Sikeres feltöltés kezelése
                })
                .addOnFailureListener(e -> {
                    // Sikertelen feltöltés kezelése
                });
    }

    public void updateCurrency(String documentId, CurrencyModel currency) {
        Map<String, Object> currencyMap = new HashMap<>();
        currencyMap.put("name", currency.getName());
        currencyMap.put("amount", currency.getAmount());

        firestore.collection("users")
                .document(userId)
                .collection("currencies")
                .document(documentId)
                .set(currencyMap)
                .addOnSuccessListener(aVoid -> {
                    // Sikeres frissítés kezelése
                })
                .addOnFailureListener(e -> {
                    // Sikertelen frissítés kezelése
                });
    }

    public void deleteCurrency(String documentId) {
        firestore.collection("users")
                .document(userId)
                .collection("currencies")
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Sikeres törlés kezelése
                })
                .addOnFailureListener(e -> {
                    // Sikertelen törlés kezelése
                });
    }

    public void getCurrencies(final FirestoreCallback<List<CurrencyDocument>> callback) {
        firestore.collection("users")
                .document(userId)
                .collection("currencies")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            callback.onFailure(error);
                            return;
                        }

                        List<CurrencyDocument> currencies = new ArrayList<>();
                        if (value != null) {
                            value.getDocuments().forEach(document -> {
                                String name = document.getString("name");
                                Double amount = document.getDouble("amount");
                                currencies.add(new CurrencyDocument(document.getId(), name, new CurrencyModel(name, amount)));
                            });
                        }

                        callback.onSuccess(currencies);
                    }
                });
    }

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}

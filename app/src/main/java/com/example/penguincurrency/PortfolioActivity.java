package com.example.penguincurrency;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PortfolioActivity extends AppCompatActivity {
    private FirestoreHelper firestoreHelper;
    private EditText amountEditText;
    private Spinner currencySpinner;
    private ListView currencyListView;
    private CurrencyListAdapter currencyListAdapter;
    private List<CurrencyDocument> currencyDocuments;
    private TextView totalValueTextView;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        requestNotificationPermission();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_portfolio);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent;
                if (itemId == R.id.nav_home) {
                    intent = new Intent(PortfolioActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    return true;
                } else if (itemId == R.id.nav_portfolio) {
                    // Már itt vagyunk
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(PortfolioActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
                return false;
            }
        });

        firestoreHelper = new FirestoreHelper();
        amountEditText = findViewById(R.id.amountEditText);
        currencySpinner = findViewById(R.id.currencySpinner);
        currencyListView = findViewById(R.id.currencyListView);
        totalValueTextView = findViewById(R.id.totalValueTextView);

        // Currency spinner beállítása
        List<String> currencies = Arrays.asList("eur", "usd", "chf", "gbp", "aud", "eth", "btc");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        // Currency list beállítása
        currencyDocuments = new ArrayList<>();
        currencyListAdapter = new CurrencyListAdapter(this, currencyDocuments, firestoreHelper, new CurrencyListAdapter.UpdateDeleteListener() {
            @Override
            public void onUpdate(CurrencyDocument currencyDocument) {
                showUpdateDialog(currencyDocument);
            }

            @Override
            public void onUpdateCurrencies() {
                loadCurrencies();
            }
        });
        currencyListView.setAdapter(currencyListAdapter);

        // Mentés gomb beállítása
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = currencySpinner.getSelectedItem().toString();
                Double amount = Double.parseDouble(amountEditText.getText().toString());
                CurrencyModel currency = new CurrencyModel(name, amount);
                firestoreHelper.addCurrency(currency);
                loadCurrencies();
            }
        });

        // Currency-k betöltése
        loadCurrencies();
    }

    private void loadCurrencies() {
        firestoreHelper.getCurrencies(new FirestoreHelper.FirestoreCallback<List<CurrencyDocument>>() {
            @Override
            public void onSuccess(List<CurrencyDocument> result) {
                currencyDocuments.clear();
                currencyDocuments.addAll(result);
                double totalValue = 0;
                for (CurrencyDocument doc : result) {
                    CurrencyModel currency = doc.getCurrency();
                    totalValue += currency.getAmount();
                }
                totalValueTextView.setText("Total Portfolio Value: " + totalValue);
                currencyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                // Hibakezelés
            }
        });
    }

    private void showUpdateDialog(CurrencyDocument currencyDocument) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Currency");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_delete, null);
        builder.setView(dialogView);

        Spinner dialogCurrencySpinner = dialogView.findViewById(R.id.dialogCurrencySpinner);
        EditText dialogAmountEditText = dialogView.findViewById(R.id.dialogAmountEditText);
        Button updateButton = dialogView.findViewById(R.id.dialogUpdateButton);

        // Beállítjuk a dialógus elemeinek értékeit
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList("eur", "usd", "chf", "gbp", "aud", "eth", "btc"));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogCurrencySpinner.setAdapter(adapter);

        CurrencyModel currency = currencyDocument.getCurrency();
        dialogCurrencySpinner.setSelection(adapter.getPosition(currency.getName()));
        dialogAmountEditText.setText(String.valueOf(currency.getAmount()));

        AlertDialog dialog = builder.create();

        // Update gomb működése
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = dialogCurrencySpinner.getSelectedItem().toString();
                Double amount = Double.parseDouble(dialogAmountEditText.getText().toString());
                CurrencyModel updatedCurrency = new CurrencyModel(name, amount);
                firestoreHelper.updateCurrency(currencyDocument.getId(), updatedCurrency);
                loadCurrencies();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // A felhasználó elutasította az engedélykérést
                // Kezeljük a hibát vagy mutassunk figyelmeztetést
            }
        }
    }
}
package com.example.penguincurrency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private TextView eur;
    private TextView usd;
    private TextView chf;
    private TextView gbp;
    private TextView aud;
    private TextView eth;
    private TextView btc;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Alapértelmezett kiválasztás
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent;
                if (itemId == R.id.nav_home) {
                    // Már itt vagyunk
                    return true;
                } else if (itemId == R.id.nav_portfolio) {
                    intent = new Intent(HomeActivity.this, PortfolioActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
                return false;
            }
        });

        eur = findViewById(R.id.eur);
        usd = findViewById(R.id.usd);
        chf = findViewById(R.id.chf);
        gbp = findViewById(R.id.gbp);
        aud = findViewById(R.id.aud);
        eth = findViewById(R.id.eth);
        btc = findViewById(R.id.btc);

        // Lekérés API-tól és megjelenítés
        fetchCurrencyData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Frissíti az árfolyam adatokat minden alkalommal, amikor az activity előtérbe kerül
        fetchCurrencyData();
    }

    private void fetchCurrencyData() {
        try {
            new FetchCurrencyData().execute("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json");
        } catch (Exception e) {
            new FetchCurrencyData().execute("https://latest.currency-api.pages.dev/v1/currencies/eur.json");
        }
    }

    private class FetchCurrencyData extends AsyncTask<String, Void, CurrencyResponse> {
        @Override
        protected CurrencyResponse doInBackground(String... urls) {
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return new Gson().fromJson(response.toString(), CurrencyResponse.class);
        }

        @Override
        protected void onPostExecute(CurrencyResponse currencyResponse) {
            if (currencyResponse != null) {
                Map<String, Double> eur = currencyResponse.getEur();
                String[] keys = eur.keySet().toArray(new String[0]);
                if (keys.length > 0) {
                    Double basePrice = eur.get("huf");
                    HomeActivity.this.eur.setText(String.format("%s: %.2f", "EUR", eur.get("huf")));
                    usd.setText(String.format("%s: %.2f", "USD", basePrice/eur.get("usd")));
                    chf.setText(String.format("%s: %.2f", "CHF", basePrice/eur.get("chf")));
                    gbp.setText(String.format("%s: %.2f", "GBP", basePrice/eur.get("gbp")));
                    aud.setText(String.format("%s: %.2f", "AUD", basePrice/eur.get("aud")));
                    eth.setText(String.format("%s: %.2f", "ETH", basePrice/eur.get("eth")));
                    btc.setText(String.format("%s: %.2f", "BTC", basePrice/eur.get("btc")));
                }
            }
        }

    }
}

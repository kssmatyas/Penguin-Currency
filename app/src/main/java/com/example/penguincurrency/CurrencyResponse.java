package com.example.penguincurrency;

import java.util.Map;

public class CurrencyResponse {
    private String date;
    private Map<String, Double> eur;

    public String getDate() {
        return date;
    }

    public Map<String, Double> getEur() {
        return eur;
    }
}
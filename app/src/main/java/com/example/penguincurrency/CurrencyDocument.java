package com.example.penguincurrency;

public class CurrencyDocument {
    private String id;
    private String name;
    private CurrencyModel currency;

    public CurrencyDocument(String id, String name, CurrencyModel currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CurrencyModel getCurrency() {
        return currency;
    }
}

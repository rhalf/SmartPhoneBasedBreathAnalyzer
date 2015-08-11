package com.rhalfcaacbay.smartphonebasedbreathanalyzer;

public class Enum {
    protected int _enumValue;

    protected Enum(int enumValue) {
        this._enumValue = enumValue;
    }

    public int Value() {
        return this._enumValue;
    }
}

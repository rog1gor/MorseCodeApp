package com.example.morsecode;

public class MorsApp {
    private static MorsApp mInstance= null;

    private int someImportantValue;

    protected MorsApp(){}

    public static synchronized MorsApp getInstance() {
        if(null == mInstance){
            mInstance = new MorsApp();
        }
        return mInstance;
    }

    public int getValue() {
        return someImportantValue;
    }

    public void setValue(int value) {
        someImportantValue = value;
    }
}
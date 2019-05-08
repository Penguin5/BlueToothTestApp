package com.example.bluetoothtestapp;

public class Globals {
    private static Globals instance;
    private static char[] charArray = new char[]{'a', 'a', 'a', 'a', 'a', 'a', 'a'};

    private Globals(){}


    public char[] getCharArr(){
        return Globals.charArray;
    }

    public void setCharAtIndex(int i, char c){
        Globals.charArray[i] = c;
    }

    public char getCharAtIndex(int i){
        return Globals.charArray[i];
    }

    public static synchronized Globals getInstance(){
        if (instance == null){
            instance = new Globals();
        }
        return instance;
    }
}

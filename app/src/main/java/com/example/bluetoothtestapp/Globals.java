package com.example.bluetoothtestapp;

public class Globals {
    private static Globals instance;
    private static String[] stringArray = new String[]{"a4", "a4","a4", "a4", "a4", "a4", "a4"};

    private Globals(){}

    /**
     * Returns the array of values that correspond to
     * @return a string array
     */
    public String[] getStringArr(){
        return Globals.stringArray;
    }

    /**
     * sets a string in the string array at a specific index
     * @param i the index to set the character at
     * @param s the string to set
     */
    public void setStringAtIndex(int i, String s){
        Globals.stringArray[i] = s;
    }

    /**
     * Gets a string from the character array at a specific index
     * @param i the index to get the character from
     * @return the string at the specified index
     */
    public String getStringAtIndex(int i){
        return Globals.stringArray[i];
    }

    /**
     * Returns an instance of the globals class
     * @return a new instance of the globals class
     */
    public static synchronized Globals getInstance(){
        if (instance == null){
            instance = new Globals();
        }
        return instance;
    }
}

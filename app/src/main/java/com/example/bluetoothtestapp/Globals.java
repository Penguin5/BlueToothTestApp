package com.example.bluetoothtestapp;

public class Globals {
    private static Globals instance;
    private static char[] charArray = new char[]{'a', 'a', 'a', 'a', 'a', 'a', 'a'};

    private Globals(){}

    /**
     * Returns the array of values that correspond to
     * @return a character array
     */
    public char[] getCharArr(){
        return Globals.charArray;
    }

    /**
     * sets a character in the character array at a specific index
     * @param i the index to set the character at
     * @param c the character to set
     */
    public void setCharAtIndex(int i, char c){
        Globals.charArray[i] = c;
    }

    /**
     * Gets a character from the character array at a specific index
     * @param i the index to get the character from
     * @return the character at the specified index
     */
    public char getCharAtIndex(int i){
        return Globals.charArray[i];
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

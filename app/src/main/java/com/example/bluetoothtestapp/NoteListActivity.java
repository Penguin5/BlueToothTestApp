package com.example.bluetoothtestapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Arrays;

public class NoteListActivity extends AppCompatActivity {
    ListView selectNotes;
    int pos;
    Globals g;
    Button pauseBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        //initializing globals
        g = Globals.getInstance();
        pauseBTN = findViewById(R.id.muteBtn);

        selectNotes = findViewById(R.id.SelectNotesList);

        ArrayAdapter<String> displayNotes = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, new String[]{
                "a4", "a5", "a#4", "a#5", "b4", "b5", "c4", "c5", "c6", "c#4", "c#5", "d4", "d5", "d#4", "d#5", "e4", "e5", "f4", "f5", "f#4", "f#5", "g4", "g5", "g#4", "g#5",});
        selectNotes.setAdapter(displayNotes);
        ExtractData();
        ChooseNote();
        pause();
    }

    private void pause() {
        pauseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              g.setStringAtIndex(pos, "pause");
                Log.d("AppInfo", "actually is a " + String.valueOf(g.getStringAtIndex(pos)));
                Log.d("AppInfo", "NoteList array is " + Arrays.toString(g.getStringArr()));
            }
        });
    }
    //method to select what note to play passed on the list view
    private void ChooseNote() {
        selectNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = selectNotes.getItemAtPosition(position).toString();
                g.setStringAtIndex(pos, s);
                Log.d("AppInfo", "set to  " + s);
                Log.d("AppInfo", "actually is a " + String.valueOf(g.getStringAtIndex(pos)));
                Log.d("AppInfo", "NoteList array is " + Arrays.toString(g.getStringArr()));
            }
        });
    }

    /**
     * method to get the intent from the main Activity
     */
    private void ExtractData() {
        Intent intent = getIntent();
        pos = intent.getIntExtra("sentPos", 0);
        Log.d("AppInfo", "The position being changed is " + pos);
    }
}

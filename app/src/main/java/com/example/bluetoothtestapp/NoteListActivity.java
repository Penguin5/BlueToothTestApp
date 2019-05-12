package com.example.bluetoothtestapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;

public class NoteListActivity extends AppCompatActivity {
    ListView selectNotes;
    int pos;
    Globals g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        //initializing globals
        g = Globals.getInstance();

        selectNotes = findViewById(R.id.SelectNotesList);

        ArrayAdapter<String> displayNotes = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, new String[]{"a", "b", "c", "d", "e", "f", "g"});
        selectNotes.setAdapter(displayNotes);
        ExtractData();
        ChooseNote();

    }

    //method to select what note to play passed on the list view
    private void ChooseNote() {
        selectNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = selectNotes.getItemAtPosition(position).toString();
                g.setCharAtIndex(pos, s.charAt(0));
                Log.d("AppInfo", "set to  " + s);
                Log.d("AppInfo", "actually is a " + String.valueOf(g.getCharAtIndex(pos)));
                Log.d("AppInfo", "NoteList array is " + Arrays.toString(g.getCharArr()));
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

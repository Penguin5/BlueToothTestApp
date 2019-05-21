package com.example.bluetoothtestapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //simple data
    Globals g;
    int i = -1;
    ArrayList<String> deviceNames = new ArrayList<>();
    //UI data
    Button pairedDevicesButton;
    ListView listView, editSwitches;
    Intent enableBluetoothIntent;
    Intent intent;
    //bluetooth stuff
    ConnectedThread connectedThread;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice[] btArray;
    //media player is for sound
    Context context = this;
    private static final UUID myUUID =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing globals
        g = Globals.getInstance();
        //initializing UI
        pairedDevicesButton = findViewById(R.id.showPairedDevicesBtn);
        listView = findViewById(R.id.ListView);
        editSwitches = findViewById(R.id.editSwitches);


        //initializing bluetooth
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        //intializing intent
        intent = new Intent(context, NoteListActivity.class);


        executeButton();
        connectBT();
        openSelectNotesAct();

    }

    //handles displaying if the two devices were able to connect
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int num = msg.arg1;
            if (!(num == 0)) {
                listPairedDevices();
            }
            return false;
        }
    });


    /**
     *
     * @param num the number indicating the state of the switch
     * @param mpA4 the corresponding note in the media player
     * @param mpA5  the corresponding note in the media player
     * @param mpASharp4 the corresponding note in the media player
     * @param mpASharp5 the corresponding note in the media player
     * @param mpB4 the corresponding note in the media player
     * @param mpB5 the corresponding note in the media player
     * @param mpC4 the corresponding note in the media player
     * @param mpC5 the corresponding note in the media player
     * @param mpC6 the corresponding note in the media player
     * @param mpCSharp4 the corresponding note in the media player
     * @param mpCSharp5 the corresponding note in the media player
     * @param mpD4 the corresponding note in the media player
     * @param mpD5 the corresponding note in the media player
     * @param mpDSharp4 the corresponding note in the media player
     * @param mpDSharp5 the corresponding note in the media player
     * @param mpE4 the corresponding note in the media player
     * @param mpE5 the corresponding note in the media player
     * @param mpF4 the corresponding note in the media player
     * @param mpF5 the corresponding note in the media player
     * @param mpFSharp4 the corresponding note in the media player
     * @param mpFSharp5 the corresponding note in the media player
     * @param mpG4 the corresponding note in the media player
     * @param mpG5 the corresponding note in the media player
     * @param mpGSharp4 the corresponding note in the media player
     * @param mpGSharp5 the corresponding note in the media player
     * @param note the note selected by the user
     */
    private void determineToPlay(int num, MediaPlayer mpA4,
                                 MediaPlayer mpA5,
                                 MediaPlayer mpASharp4,
                                 MediaPlayer mpASharp5,
                                 MediaPlayer mpB4,
                                 MediaPlayer mpB5,
                                 MediaPlayer mpC4,
                                 MediaPlayer mpC5,
                                 MediaPlayer mpC6,
                                 MediaPlayer mpCSharp4,
                                 MediaPlayer mpCSharp5,
                                 MediaPlayer mpD4,
                                 MediaPlayer mpD5,
                                 MediaPlayer mpDSharp4,
                                 MediaPlayer mpDSharp5,
                                 MediaPlayer mpE4,
                                 MediaPlayer mpE5,
                                 MediaPlayer mpF4,
                                 MediaPlayer mpF5,
                                 MediaPlayer mpFSharp4,
                                 MediaPlayer mpFSharp5,
                                 MediaPlayer mpG4,
                                 MediaPlayer mpG5,
                                 MediaPlayer mpGSharp4,
                                 MediaPlayer mpGSharp5,
                                 String note) {

        if (num == 1) {
            if (note.equals("a4")) {
                mpA4.start();
            } else if (note.equals("a5")) {
                mpA5.start();
            } else if (note.equals("a#4")) {
                mpASharp4.start();
            } else if (note.equals("a#5")) {
                mpASharp5.start();
            } else if (note.equals("b4")) {
                mpB4.start();
            } else if (note.equals("b5")) {
                mpB5.start();
            } else if (note.equals("c4")) {
                mpC4.start();
            } else if (note.equals("c5")) {
                mpC5.start();
            } else if (note.equals("c6")) {
                mpC6.start();
            } else if (note.equals("c#4")) {
                mpCSharp4.start();
            } else if (note.equals("c#5")) {
                mpCSharp5.start();
            } else if (note.equals("d4")) {
                mpD4.start();
            } else if (note.equals("d5")) {
                mpD5.start();
            } else if (note.equals("d#4")) {
                mpDSharp4.start();
            } else if (note.equals("d#5")) {
                mpDSharp5.start();
            } else if (note.equals("e4")) {
                mpE4.start();
            } else if (note.equals("e5")) {
                mpE5.start();
            } else if (note.equals("f4")) {
                mpF4.start();
            } else if (note.equals("f5")) {
                mpF5.start();
            } else if (note.equals("f#4")) {
                mpFSharp4.start();
            } else if (note.equals("f#5")) {
                mpFSharp5.start();
            } else if (note.equals("g4")) {
                mpG4.start();
            } else if (note.equals("g5")) {
                mpG5.start();
            } else if (note.equals("g#4")) {
                mpGSharp4.start();
            } else if (note.equals("g#5")) {
                mpGSharp5.start();
            }
        }
    }


    /**
     * method that lists all the currently paired devices
     */
    private void listPairedDevices() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
        editSwitches.setAdapter(arrayAdapter);
    }

    /**
     * method to open up new activity that manages what note to play for each device
     */
    private void openSelectNotesAct() {
        editSwitches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int indexBeingSent, long id) {
                Log.d("AppInfo", "Position being sent is " + indexBeingSent);
                intent.putExtra("sentPos", indexBeingSent);
                startActivity(intent);
            }
        });

    }

    /**
     * method to run the connect thread, passes the bluetooth device from an array based on the list view
     */
    private void connectBT() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConnectThread connectThread = new ConnectThread(btArray[position]);
                connectThread.start();
            }
        });

    }

    /**
     * in charge of listing previously paired devices
     */
    private void executeButton() {
        pairedDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AppInfo", "clicked Show Paired Devices");
                Set<BluetoothDevice> bondedDevices = myBluetoothAdapter.getBondedDevices();
                String[] bondedNames = new String[bondedDevices.size()];
                btArray = new BluetoothDevice[bondedDevices.size()];
                int index = 0;

                //just loops through all the bonded devices and puts them into an array and displays it
                if (bondedDevices.size() > 0) {
                    for (BluetoothDevice device : bondedDevices) {
                        bondedNames[index] = device.getName();
                        btArray[index] = device;
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, bondedNames);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
    }

    /**
     * opens a client socket
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String nameOfDevice;

        /**
         * Constructs a new ConnectThread Object
         *
         * @param device the device that is trying to be connected to
         */
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(myUUID);
                nameOfDevice = device.getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        /**
         * method that runs when the thread is run
         * This method establishes a connection with the server
         */
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            myBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            i = i + 1;
            Log.d("AppInfo", "The index being used is " + i);
            deviceNames.add(nameOfDevice);

            Message message = Message.obtain();
            message.arg1 = 1;
            handler1.sendMessage(message);

            connectedThread = new ConnectedThread(mmSocket, i);
            connectedThread.start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //methods in charge of sending and receiving data
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final DataInputStream mmInStream;
        private final DataOutputStream mmOutStream;
        int localI;

        /**
         * contructs the connected thread
         *
         * @param socket the bluetooth socket used for connection
         * @param num    the device number that corresponds to the character array for the notes
         */
        public ConnectedThread(BluetoothSocket socket, int num) {
            mmSocket = socket;
            DataInputStream tmpIn = null;
            DataOutputStream tmpOut = null;

            localI = num;
            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                tmpOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        /**
         * This method runs when the thread is created
         */
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()


            int tempNum;
            String stringToUse;

            MediaPlayer mpA4;
            MediaPlayer mpA5;
            MediaPlayer mpASharp4;
            MediaPlayer mpASharp5;
            MediaPlayer mpB4;
            MediaPlayer mpB5;
            MediaPlayer mpC4;
            MediaPlayer mpC5;
            MediaPlayer mpC6;
            MediaPlayer mpCSharp4;
            MediaPlayer mpCSharp5;
            MediaPlayer mpD4;
            MediaPlayer mpD5;
            MediaPlayer mpDSharp4;
            MediaPlayer mpDSharp5;
            MediaPlayer mpE4;
            MediaPlayer mpE5;
            MediaPlayer mpF4;
            MediaPlayer mpF5;
            MediaPlayer mpFSharp4;
            MediaPlayer mpFSharp5;
            MediaPlayer mpG4;
            MediaPlayer mpG5;
            MediaPlayer mpGSharp4;
            MediaPlayer mpGSharp5;

            mpA4 = MediaPlayer.create(context, R.raw.a4actual);
            mpA5 = MediaPlayer.create(context, R.raw.a5actual);
            mpASharp4 = MediaPlayer.create(context, R.raw.asharp4actual);
            mpASharp5 = MediaPlayer.create(context, R.raw.asharp5actual);

            mpB4 = MediaPlayer.create(context, R.raw.b4actual);
            mpB5 = MediaPlayer.create(context, R.raw.b5actual);

            mpC4 = MediaPlayer.create(context, R.raw.c4actual);
            mpC5 = MediaPlayer.create(context, R.raw.c5actual);
            mpC6 = MediaPlayer.create(context, R.raw.c6actual);
            mpCSharp4 = MediaPlayer.create(context, R.raw.csharp4actual);
            mpCSharp5 = MediaPlayer.create(context, R.raw.csharp5actual);

            mpD4 = MediaPlayer.create(context, R.raw.d4actual);
            mpD5 = MediaPlayer.create(context, R.raw.d5ectual);
            mpDSharp4 = MediaPlayer.create(context, R.raw.dsharp4actual);
            mpDSharp5 = MediaPlayer.create(context, R.raw.dsharp5actual);

            mpE4 = MediaPlayer.create(context, R.raw.e4actual);
            mpE5 = MediaPlayer.create(context, R.raw.e5actual);

            mpF4 = MediaPlayer.create(context, R.raw.f4actual);
            mpF5 = MediaPlayer.create(context, R.raw.f5actual);
            mpFSharp4 = MediaPlayer.create(context, R.raw.fsharp4actual);
            mpFSharp5 = MediaPlayer.create(context, R.raw.fsharp5actual);

            mpG4 = MediaPlayer.create(context, R.raw.g4actual);
            mpG5 = MediaPlayer.create(context, R.raw.g5actual);
            mpGSharp4 = MediaPlayer.create(context, R.raw.gsharp4actual);
            mpGSharp5 = MediaPlayer.create(context, R.raw.gsharp5actual);


            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

                        //a bunch of stuff that takes the data and gets numeric value of the first character,
                        // which is either a zero or one
                        tempNum = Character.getNumericValue(new String(buffer, "UTF-8").charAt(0));
                        stringToUse = g.getStringAtIndex(localI);
                        Log.d("AppInfo", String.valueOf(new String(buffer, "UTF-8")));
                        Log.d("AppInfo", "Playing at an index of " + localI);
                        Log.d("AppInfo", "playing note: " + String.valueOf(stringToUse));

                        determineToPlay(tempNum, mpA4,
                                mpA5,
                                mpASharp4,
                                mpASharp5,
                                mpB4,
                                mpB5,
                                mpC4,
                                mpC5,
                                mpC6,
                                mpCSharp4,
                                mpCSharp5,
                                mpD4,
                                mpD5,
                                mpDSharp4,
                                mpDSharp5,
                                mpE4,
                                mpE5,
                                mpF4,
                                mpF5,
                                mpFSharp4,
                                mpFSharp5,
                                mpG4,
                                mpG5,
                                mpGSharp4,
                                mpGSharp5, stringToUse);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /**
         * Call this from the main activity to send data to the remote device.
         *
         * @param bytes the bytes to write
         */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}


package com.example.bluetoothtestapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    //simple data
    Globals g;
    int  i = -1;
    ArrayList<String> deviceNames = new ArrayList<>();
    //UI data
    Button  pairedDevicesButton, listenButton, signalButton;
    TextView isClicked, signalRecieved;
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
        listenButton = findViewById(R.id.listenBtn);
        signalButton = findViewById(R.id.signalBtn);
        signalRecieved = findViewById(R.id.recieveSignal);
        isClicked = findViewById(R.id.isClicked);


        //initializing bluetooth
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        //intializing intent
        intent = new Intent(context, NoteListActivity.class);



        executeButton();
        connectBT();
        listenButton();
        sendSignal();
        openSelectNotesAct();

    }

    //handles displaying if the two devices were able to connect
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int num = msg.arg1;
            if (!(num == 0)){
               isClicked.setText("Connected");
               listPairedDevices();
           }
            return false;
        }
    });

    //handles displaying if the signal is being sent
    //not being used currently
    Handler handler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int num = msg.arg1;
           // playNote(num);
            return false;
        }
    });

    //plays the determined note
    private void playNote(int num, MediaPlayer mpA, MediaPlayer mpB, MediaPlayer mpC, MediaPlayer mpD, char c){

        if (num == 1){
            determineNote(c, mpA, mpB, mpC, mpD);
        } else {
            if(mpA.isPlaying()) {
                mpA.pause();
            }
            if(mpB.isPlaying()) {
                mpB.pause();
            }
            if(mpC.isPlaying()) {
                mpC.pause();
            }
            if(mpD.isPlaying()) {
                mpD.pause();
            }

        }
    }

    //determines the note to play
    //needs to be edited for multiple devices
    private void determineNote(char note, MediaPlayer mpA, MediaPlayer mpB, MediaPlayer mpC, MediaPlayer mpD){
        if (note == 'a') {
            mpA.start();
            Log.d("AppInfo", "a is playing");
        }else if (note == 'b'){
            mpB.start();
            Log.d("AppInfo", "b is playing");
        }else if (note == 'c'){
            mpC.start();
            Log.d("AppInfo", "c is playing");
        }else if (note == 'd'){
            mpD.start();
            Log.d("AppInfo", "song is playing");
        }
    }

    //starts the bluetooth socket server
    private void listenButton() {
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptThread acceptThread = new AcceptThread();
                acceptThread.start();
            }
        });
    }

    //method to send data from one device to another
    @SuppressLint("ClickableViewAccessibility")
    private void sendSignal() {
        signalButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        connectedThread.write("100".getBytes());
                        Log.d("AppInfo", "Button held down");
                        return true;
                    case MotionEvent.ACTION_UP:
                        connectedThread.write("000".getBytes());
                        return true;
                }
                return false;
            }
        });

    }

    //method that lists all the currently paired devices
    private void listPairedDevices (){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
        editSwitches.setAdapter(arrayAdapter);
    }


   //method to open up new activity that manages what note to play for each device
   private void openSelectNotesAct(){
            editSwitches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int indexBeingSent, long id) {
                    Log.d("AppInfo", "Position being sent is " + indexBeingSent);
                    intent.putExtra("sentPos", indexBeingSent);
                    startActivity(intent);
                }
            });

    }

    //method to run the connect thread, passes the bluetooth device from an array based on the list view
    private void connectBT() {
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               ConnectThread connectThread = new ConnectThread(btArray[position]);
               connectThread.start();
           }
       });

    }

    //in charge of listing previously paired devices
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
                if (bondedDevices.size() > 0){
                    for (BluetoothDevice device : bondedDevices)
                    {
                        bondedNames[index] = device.getName();
                        btArray[index] = device;
                        index ++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, bondedNames);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
    }

    //opens a bluetooth socket server
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = myBluetoothAdapter.listenUsingRfcommWithServiceRecord("DEVICE !",myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Send signal manages the task for the app to do
                    connectedThread = new ConnectedThread(socket, 1);
                    connectedThread.start();

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //opens a client socket
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String nameOfDevice;

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
            i = i +1;
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

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()


            int tempNum;
            char charToUse;

            MediaPlayer mpA;
            MediaPlayer mpB;
            MediaPlayer mpC;
            MediaPlayer mpD;


            mpA = MediaPlayer.create(context, R.raw.a);
            mpA.setLooping(true);
            mpB = MediaPlayer.create(context, R.raw.b);
            mpB.setLooping(true);
            mpC = MediaPlayer.create(context, R.raw.c);
            mpC.setLooping(true);
            mpD = MediaPlayer.create(context, R.raw.song);
            mpD.setLooping(true);

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
                        charToUse = g.getCharAtIndex(localI);
                        Log.d("AppInfo", String.valueOf(new String(buffer, "UTF-8").charAt(0)));
                        Log.d("AppInfo", "Playing at an index of " + localI);
                        Log.d("AppInfo", "playing note: " + String.valueOf(charToUse));

                        playNote(tempNum, mpA, mpB, mpC, mpD, charToUse);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}


package com.example.bluetoothtestapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    //this is a test
    Button buttonON, buttonOFF, pairedDevicesButton, listenButton, signalButton;
    TextView isClicked, signalRecieved;
    ListView listView;
    Intent enableBluetoothIntent;
    int REQUEST_ENABLE_BT;

    ConnectedThread connectedThread;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice[] btArray;
    //media player is for sound
    private MediaPlayer mp;


    private static final UUID myUUID =
            UUID.fromString("9fa718be-5b37-11e9-8647-d663bd873d93");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        buttonON = findViewById(R.id.btON);
        buttonOFF = findViewById(R.id.btOFF);
        pairedDevicesButton = findViewById(R.id.showPairedDevicesBtn);
        listView = findViewById(R.id.ListView);
        isClicked = findViewById(R.id.isClicked);
        listenButton = findViewById(R.id.listenBtn);
        signalButton = findViewById(R.id.signalBtn);
        signalRecieved = findViewById(R.id.recieveSignal);
        mp = MediaPlayer.create(this, R.raw.a);

        enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        REQUEST_ENABLE_BT = 1;


        bluetoothONMethod();
        bluetoothOFFMethod();
        executeButton();
        connectBT();
        listenButton();
        sendSignal();

    }

    //handles displaying if the two devices were able to connect
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int num = msg.arg1;
            if (!(num == 0)){
               isClicked.setText("Connected");
           }
            return false;
        }
    });

    //handles displaying if the signal is being sent
    Handler handler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int num = msg.arg1;
            if (num == 1){
                signalRecieved.setText("hello");
                mp.setLooping(true);
                mp.start();
            } else {
                signalRecieved.setText("nothing");
                mp.pause();
            }
            return false;
        }
    });

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
                        connectedThread.write(true);
                        Log.d("AppInfo", "Button held down");
                        return true;
                    case MotionEvent.ACTION_UP:
                        connectedThread.write(false);
                        return true;
                }
                return false;
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

    //in charge of notifying the user when they enable bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode ==  REQUEST_ENABLE_BT)
        {
            if(requestCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(), "Bluetooth enabling was cancel", Toast.LENGTH_LONG).show();
            }
        }
    }

    //method that controls turning the bluetooth on
    private void bluetoothONMethod()
    {
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myBluetoothAdapter == null){
                    //bluetooth is not supported
                    Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this Device", Toast.LENGTH_LONG).show();
                } else{
                    //check if device has bluetooth disabled, if it does, run code in the brackets
                    if(!myBluetoothAdapter.isEnabled()){
                        //enables bluetooth

                        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                    }
                }
            }
        });
    }

    //method that controls turning the bluetooth off
    private void bluetoothOFFMethod() {
        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myBluetoothAdapter.isEnabled()){
                    myBluetoothAdapter.disable();
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
                    connectedThread = new ConnectedThread(socket);
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


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(myUUID);
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
            Message message = Message.obtain();
            message.arg1 = 1;
            handler1.sendMessage(message);

            connectedThread = new ConnectedThread(mmSocket);
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

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            DataInputStream tmpIn = null;
            DataOutputStream tmpOut = null;

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
            boolean stateOfButton;
            int tempNum;
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    stateOfButton = mmInStream.readBoolean();
                    if(stateOfButton){
                        Log.d("AppInfo", "Button Being read as true");
                       tempNum = 1;
                    } else {
                        tempNum = 0;
                        Log.d("AppInfo", "Button Being read as false");
                    }

                    // Send the obtained bytes to the UI activity.
                    Message message = Message.obtain();
                    message.arg1 = tempNum;
                    handler2.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(boolean bool) {
            try {
                Log.d("AppInfo", "button being sent as" + String.valueOf(bool));
                mmOutStream.writeBoolean(bool);
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


package com.example.bluetoothtestapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    //this is a test
    Button buttonON, buttonOFF, pairedDevicesButton, listenButton;
    TextView isClicked;
    ListView listView;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice[] btArray;
    Intent enableBluetoothIntent;
    int REQUEST_ENABLE_BT;

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


        enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        REQUEST_ENABLE_BT = 1;


        bluetoothONMethod();
        bluetoothOFFMethod();
        executeButton();
        connectBT();
        listenButton();


    }

    private void listenButton() {
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptThread acceptThread = new AcceptThread();
                acceptThread.start();
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
           isClicked.setText(String.valueOf(msg.arg1));
            return false;
        }
    });

    private void connectBT() {
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               ConnectThread connectThread = new ConnectThread(btArray[position]);
               connectThread.start();
              // Toast.makeText(getApplicationContext(), "connected to other device", Toast.LENGTH_LONG).show();
           }
       });

    }


    //in charge of listing previously paired devices
    private void executeButton() {
              pairedDevicesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, bondedNames);
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
                    // A connection was accepted. Perform work associated with
                    //manageMyConnectedSocket(mmSocket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
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
            handler.sendMessage(message);

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
}

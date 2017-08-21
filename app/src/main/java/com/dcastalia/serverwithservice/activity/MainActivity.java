package com.dcastalia.serverwithservice.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dcastalia.serverwithservice.R;
import com.dcastalia.serverwithservice.Utils.Constant;
import com.dcastalia.serverwithservice.Utils.MyApplication;
import com.dcastalia.serverwithservice.Utils.Utils;
import com.dcastalia.serverwithservice.boardcastreceiver.ConnectivityReceiver;
import com.dcastalia.serverwithservice.service.MainServiceForServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{

    private TextView  serverText , portText , clientList , messageText ;
    private Button startServerButton , stopServerButton , messageButton , getListButton ;
    private String serverIp;
    private View serverPort  ;
    private final String TAG = MainActivity.class.getSimpleName();
    private boolean binded=false;
    private MainServiceForServer serverService;



    /** This Boardcast Recevier is triggered to get the data from the service **/

    private final BroadcastReceiver serviceMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action.equals(Constant.ACTION_MESSAGE)){
                // Toast.makeText(context,, Toast.LENGTH_SHORT).show();
                String message = intent.getStringExtra(Constant.WELCOME_MESSAGE_KEY);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                messageText.setText(message);

            }

        }
    };



    // ServiceConnection For Communicating With Service
    ServiceConnection serverServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainServiceForServer.LocalServerBinder binder = (MainServiceForServer.LocalServerBinder) service;
            serverService = binder.getService();
            binded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binded = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

    }


    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

        /** Registering BoardCast To Receive Message From The Service **/
        IntentFilter intentFilter = new IntentFilter(Constant.ACTION_MESSAGE);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(serviceMessageReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /** Must unRegister Receiver When Not Using **/
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(serviceMessageReceiver);
    }

    private void findViews() {

        /** Find all views and bind them in local variable to user in future ; **/

        serverText = (TextView) findViewById(R.id.txt_server_ip);
        portText = (TextView) findViewById(R.id.txt_port);
        startServerButton = (Button) findViewById(R.id.btnStartServer);
        stopServerButton = (Button) findViewById(R.id.btnStopSerer);
        messageButton = (Button) findViewById(R.id.btnMessage);
        clientList = (TextView) findViewById(R.id.textClientList);
        getListButton = (Button) findViewById(R.id.btnGetList);
        messageText = (TextView) findViewById(R.id.textClientMessage);


        /*** disable some startUp Button **/
        disableSomeButton();


        /** Get Client List From The Service **/
        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String clientListString  = MainActivity.this.serverService.getClientList().toString() ;
                    if(clientListString!=null){
                        clientList.setText(clientListString);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

      messageButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            MainActivity.this.serverService.sendMessage(" A Sample Text To Send .....");
          }
      });



    }

    private void disableSomeButton() {
        stopServerButton.setEnabled(false);
        messageButton.setEnabled(false);
    }





    /** Do Start Server Work Here. This is a actionPerforming Method for button start Server **/
    public void startServer(View view){
        Log.d(TAG , "Server Starting........");
        checkConnection();

    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        manageServerService(isConnected);
    }

    private void manageServerService(boolean isConnected) {
        if(isConnected){
            Utils.longToast(this,  "Wifi Available ! Starting Service");
            startServerService();
        }
        else{
            Utils.longToast(this, "Disconnected ! Stopping Service ");
            stopServerService();

        }
        setServerIp(isConnected);
    }

    private void setServerIp(boolean isConnected) {

        /** If Internet is available than set the serverIp and Port otherwise set deafault **/
        if(isConnected){
            serverText.setText(Utils.getWifiIpAddress(this));
            portText.setText(String.valueOf(Constant.SERVER_MAIN_PORT));
        }
        else{
            serverText.setText(getResources().getString(R.string.not_connected));
            portText.setText(getResources().getString(R.string.no_port));
        }
    }

    private void stopServerService() {

            if (binded) {
                // Unbind Service
                this.unbindService(serverServiceConnection);
                binded = false;
                Utils.log("Service Stopped For Network");
            }

            // Disable Stop Server Button and Message Button and Enable Start Server Button

            messageButton.setEnabled(false);
            stopServerButton.setEnabled(false);
            startServerButton.setEnabled(true);



    }

    private void startServerService() {

            // Create Intent object for WeatherService.
            Intent intent = new Intent(this, MainServiceForServer.class);
            // Call bindService(..) method to bind service with UI.
            this.bindService(intent, serverServiceConnection, Context.BIND_AUTO_CREATE);
            Utils.log("Server Service Starting...");

            //Disable Start Server Button And Enable Stop and Message Button
            messageButton.setEnabled(true);
            stopServerButton.setEnabled(true);
            startServerButton.setEnabled(false);



    }

    /** This method perform button click of stop Server **/
    public void stopServer(View view){
      stopServerService();
    }
    public void goMessageActivity(View view){

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
            manageServerService(isConnected);
    }
}

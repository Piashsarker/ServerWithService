package com.dcastalia.serverwithservice.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dcastalia.serverwithservice.service.MainServiceForServer;
import com.dcastalia.serverwithservice.Utils.MyApplication;
import com.dcastalia.serverwithservice.R;
import com.dcastalia.serverwithservice.Utils.Utils;
import com.dcastalia.serverwithservice.boardcastreceiver.ConnectivityReceiver;

public class MainActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{

    private TextView  serverText , portText ;
    private Button startServerButton , stopServerButton , messageButton ;
    private String serverIp;
    private View serverPort  ;
    private final String TAG = MainActivity.class.getSimpleName();
    private boolean binded=false;
    private MainServiceForServer serverService;

    ServiceConnection weatherServiceConnection = new ServiceConnection() {

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
    }

    private void findViews() {

        /** Find all views and bind them in local variable to user in future ; **/

        serverText = (TextView) findViewById(R.id.txt_server_ip);
        serverPort = findViewById(R.id.txt_port);
        startServerButton = (Button) findViewById(R.id.btnStartServer);
        stopServerButton = (Button) findViewById(R.id.btnStopSerer);
        messageButton = (Button) findViewById(R.id.btnMessage);

        /*** disable some startUp Button **/
        disableSomeButton();
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
            Utils.longToast(this,  "Connection Available For Starting Service");
            startServerService();
        }
        else{
            Utils.longToast(this, "Connection Down , Closing All Service and Threads");
            stopServerService();
        }
    }

    private void stopServerService() {

            if (binded) {
                // Unbind Service
                this.unbindService(weatherServiceConnection);
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
            this.bindService(intent, weatherServiceConnection, Context.BIND_AUTO_CREATE);
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

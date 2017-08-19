package com.dcastalia.serverwithservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView  serverText , portText ;
    private Button startServerButton , stopServerButton , messageButton ;
    private String serverIp;
    private View serverPort  ;
    private final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();


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


    }
    public void stopServer(View view){

    }
    public void goMessageActivity(View view){

    }

}

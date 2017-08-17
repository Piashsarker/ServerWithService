package com.dcastalia.serverwithservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;

public class MyService extends Service {

    private final int PORT = 5555;
    private static ServerSocket serverSocket  =null ;
    private static Socket clientSocket = null ;
    private static final int maxClientsCount = 30 ;
    private static final ClientThread[] clientThread = new ClientThread[maxClientsCount];
    private static final String TAG = "MyService";



    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG , "Server Service Waiting To Get Started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       startServer();
      return  START_STICKY ;
    }

    private void startServer() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

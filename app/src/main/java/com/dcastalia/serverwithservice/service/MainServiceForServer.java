package com.dcastalia.serverwithservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dcastalia.serverwithservice.thread.ClientThread;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServiceForServer extends Service {

    private static  final String TAG = "MainServiceForServer";

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];

    private static boolean serviceRunning = false ;


    private IBinder binder = new LocalServerBinder();

    public class LocalServerBinder extends Binder{
        public MainServiceForServer getService(){
           return    MainServiceForServer.this;
        }
    }

    public MainServiceForServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBinder() Called");
        serviceRunning = true ;
        // Start Server Thread For Listening Client Connection .

        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() Called");
        serviceRunning = false ;
        return  true ;

    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind() Called");
        super.onRebind(intent);

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() Called");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() Called");
        return START_NOT_STICKY ;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy  Called");
        serviceRunning = false ;
        super.onDestroy();
    }







}

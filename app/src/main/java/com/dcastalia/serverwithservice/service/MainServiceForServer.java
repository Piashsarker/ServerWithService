package com.dcastalia.serverwithservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dcastalia.serverwithservice.thread.ServerThread;

import java.io.IOException;

public class MainServiceForServer extends Service {

    private static  final String TAG = "MainServiceForServer";
    private static boolean serviceRunning = false ;
    private static ServerThread serverThread ;
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
        serverThread = new ServerThread(serviceRunning);
        serverThread.start();
        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() Called");
         serverThread.closeServer();
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
        serverThread.closeServer();
        super.onDestroy();
    }


    public String getClientList() throws IOException {
        return  serverThread.printAllClient();
    }





}

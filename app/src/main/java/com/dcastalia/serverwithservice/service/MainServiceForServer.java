package com.dcastalia.serverwithservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dcastalia.serverwithservice.Utils.Constant;
import com.dcastalia.serverwithservice.thread.ServerThread;

import java.io.IOException;

public class MainServiceForServer extends Service implements ServerThread.ClientMessageReceivedListener {

    private static  final String TAG = "MainServiceForServer";
    private static boolean serviceRunning = false ;
    public static ServerThread serverThread ;
    private String message ;
    private IBinder binder = new LocalServerBinder();

    public void sendMessage(String s) {
        serverThread.sendToAllClient(s);
    }

    @Override
    public void clientMessageReceived(String message) {

        Intent intent = new Intent(Constant.ACTION_MESSAGE);
        intent.putExtra(Constant.WELCOME_MESSAGE_KEY, message);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(intent);
    }


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
        serverThread = new ServerThread(serviceRunning ,this);
        serverThread.start();
        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() Called");
        try {
            serverThread.closeServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            serverThread.closeServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    public String getClientList() throws IOException {
        return  serverThread.printAllClient();
    }







}

package com.dcastalia.serverwithservice.thread;

import com.dcastalia.serverwithservice.Utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by piashsarker on 8/17/17.
 */

public class ClientThread extends  Thread {

    private DataInputStream dataInputStream = null;
    private PrintStream printStream = null;
    private DataOutputStream dataOutputStream = null ;
    private  PrintWriter printWriter = null;
    public Socket clientSocket = null;
    public final ClientThread[] threads;


    private int maxClientsCount;


    public ClientThread(Socket clientSocket , ClientThread[] clientThreads){
        this.threads = clientThreads ;
        this.clientSocket = clientSocket;
        maxClientsCount = threads.length;
    }

    @Override
    public void run() {
        try {
            if(!clientSocket.isClosed()){
                dataInputStream = new DataInputStream(clientSocket.getInputStream());
                printStream = new PrintStream(clientSocket.getOutputStream());
                dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public boolean sendData(String data){
        boolean isSent  = false;
        try{
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            printWriter.println(data);
            Utils.log("Data Sending"+ data);
            isSent = true;
        }
        catch (IOException ex){
            ex.printStackTrace();
            isSent = false ;
            printWriter.flush();
            printWriter.close();
        }
        return  isSent ;
    }

    public String  receiveData() throws IOException {
        String data = "";
        BufferedReader input = null  ;
        try{
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
           if(input.readLine()!=null){
               data = input.readLine();
               Utils.log(" Message From Client "+ data);
           }
            input.close();
        }
        catch (IOException ex){
            input.close();
        }
        return  data ;
    }



   public void closeClientSocket() throws IOException{

    /** Close Socket If Connection Is Lost **/

     if(clientSocket!=null && dataInputStream!=null && dataOutputStream!=null && printStream!=null){
        clientSocket.close();
        dataInputStream.close();
        dataOutputStream.close();
        printStream.close();
     }

   }


}

package com.dcastalia.serverwithservice.thread;

import com.dcastalia.serverwithservice.Utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by piashsarker on 8/17/17.
 */

public class ClientThread extends  Thread  {

    private PrintStream printStream = null;
    private  PrintWriter printWriter = null;
    public Socket clientSocket;
    private BufferedReader bufferedIn ;
    public final ClientThread[] threads;
    public boolean dataReceiving = false;
    private String message = null ;
    private ServerThread.ClientMessageReceivedListener clientMessageReceivedListener ;


    private int maxClientsCount;


    public ClientThread(Socket clientSocket , ClientThread[] clientThreads, ServerThread.ClientMessageReceivedListener clientMessageReceivedListener){
        this.threads = clientThreads ;
        this.clientSocket = clientSocket;
        maxClientsCount = threads.length;
        dataReceiving = true ;
        this.clientMessageReceivedListener= clientMessageReceivedListener;

    }

    @Override
    public void run() {
        try{

            while (dataReceiving){
                /** this code  will listen the incoming message for each client **/
                if(!clientSocket.isClosed()){
                    bufferedIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    if(bufferedIn.read()!=-1){
                        message = bufferedIn.readLine();
                        if(message!=null && clientMessageReceivedListener!=null){
                            Utils.log("Message Received : " + message + " from "+ clientSocket.getInetAddress().getHostAddress());

                            /** Pass the message to server  interface so that the class who implement it can listen message **/
                            clientMessageReceivedListener.clientMessageReceived(message);
                        }
                    }
                    else{
                        Utils.log("Client Disconnected");
                        clientSocket.close();
                        this.interrupt();
                        dataReceiving= false ;
                    }

                }

            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }



    public boolean sendData(String data){
        boolean isSent  = false;
        try{
            if(clientSocket!=null && !clientSocket.isClosed()){
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
                printWriter.println(data);
                Utils.log("Data Sending"+ data);
                printWriter.flush();
                isSent = true;
            }
            else{
                Utils.log("Client Not Online "+ clientSocket.getInetAddress().getHostAddress());
            }

        }
        catch (IOException ex){
            ex.printStackTrace();
            try {
                closeClientSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isSent = false ;
        }
        return  isSent ;
    }



   public void closeClientSocket() throws IOException{

    /** Close Socket If Connection Is Lost **/
    Utils.log("Client Socket Closing ");
      dataReceiving = false;
     if(!clientSocket.isClosed() && clientSocket!=null){
         printWriter.close();
         bufferedIn.close();
         clientSocket.close();
     }


   }


}

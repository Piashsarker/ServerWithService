package com.dcastalia.serverwithservice.thread;

import android.graphics.Bitmap;

import com.dcastalia.serverwithservice.Utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.System.out;

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
    private boolean bitmapReceiving = false;
    private OutputStream outputStream= null ;
    private DataOutputStream dos =null ;


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
                        dataReceiving= false ;
                    }

                }

            }
            //Receive The Bitmap Here
            while(bitmapReceiving){

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
                Utils.log("Data Sending"+ data + " Length "+data.length());

                printWriter.flush();
                isSent = true;
            }
            else{
                Utils.log("Client Not Online ");
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

    public void sendBitmap(Bitmap bitmap){

            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream );
                byte[] outBuffer = stream.toByteArray();
                sendBytes(outBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    public void sendBytes(byte[] myByteArray) throws IOException {
        sendBytes(myByteArray, 0, myByteArray.length);
    }

    public void sendBytes(byte[] myByteArray, int start, int len) throws IOException {

        try{
            if (len < 0)
                throw new IllegalArgumentException("Negative length not allowed");
            if (start < 0 || start >= myByteArray.length)
                throw new IndexOutOfBoundsException("Out of bounds: " + start);

            Utils.log(" Byte Length "+len);
            outputStream = clientSocket.getOutputStream();
            dos = new DataOutputStream(out);

            dos.writeInt(len);
            if (len > 0) {
                dos.write(myByteArray, start, len);
                dos.flush();
                Utils.log("Sending Bitmap To Client ");
            }
        }
        catch (IOException io){
            io.printStackTrace();
        }


    }


   public void closeClientSocket() throws IOException{

    /** Close Socket If Connection Is Lost **/
    Utils.log("Client Socket Closing ");
      dataReceiving = false;
       bitmapReceiving = false;
     if(!clientSocket.isClosed() && clientSocket!=null){
         printWriter.close();
         bufferedIn.close();
         clientSocket.close();
     }


   }


}

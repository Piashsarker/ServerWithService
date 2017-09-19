package com.dcastalia.serverwithservice.thread;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.dcastalia.serverwithservice.Utils.Constant;
import com.dcastalia.serverwithservice.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by piashsarker on 8/17/17.
 */

public class ClientThread extends  Thread  {

    public Socket clientSocket;
    public final ClientThread[] threads;
    public boolean dataReceiving = false;
    private String message ;
    private String command ;
    private ServerThread.ClientMessageReceivedListener clientMessageReceivedListener ;
    private boolean bitmapReceiving = false;
    private DataOutputStream dataOutputStream =null ;
    private DataInputStream inputStream = null ;
    private String ipAddress = null ;
    private boolean isReachable = false ;



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

                ipAddress = clientSocket.getInetAddress().getHostAddress();

                Utils.log("Client Thread Running For Client "+ipAddress);
                // initialize dataInputStream for listening incoming stream  and dataOutPut stream for sending outgoing stream .
                inputStream = new DataInputStream(clientSocket.getInputStream());
                //initailize dataOutPutStream for sending stream


                while (dataReceiving) {
                    /** this code  will listen the incoming message for each client **/
                    Utils.log("Data Receiving  Thread Running For Client .. " + ipAddress);



                    if (inputStream.read() != -1) {
                        command = inputStream.readUTF();
                        Utils.log("Message Received : " + command);

                        /** Read the command and manages the sequece of reading data from the input stream , if sequence breaks
                         * than the program will be stuck. Must manage the sequences . We are working with two type of data String , Byte Array .
                         * #Check which types of data are the client sending
                         * if byte[] than it's bitmap of screen sharing otherwise all command and data are
                         * string
                         */

                        if(command.equals(Constant.STRING_TYPE_DATA)){
                           String message = inputStream.readUTF();
                            Utils.log("Message From Client "+message);
                           if(message!=null && clientMessageReceivedListener!=null) {
                               /** Pass the message to service and let service process action based on it**/
                             clientMessageReceivedListener.clientMessageReceived(message);
                           }
                        }
                        if(command.equals(Constant.BYTE_ARRAY_DATA)){
                            /** This byte array consist the data of bitmap images **? First read is for length and second is for byte array  */
                           int length = inputStream.readInt() ;
                            /** Read the full byte from the inputStream using readFully()**/

                            if(length>0){
                                byte[] bitmapArray = new byte[length];
                                inputStream.readFully(bitmapArray,0, bitmapArray.length);
                                /** Send the @bitmapArray to service and let service process it for each request **/
                            }

                        }





                    } else {
                        clientMessageReceivedListener.clientMessageReceived("Client Disconnected " + ipAddress);
                        Utils.log("Client Disconnected " + ipAddress);
                        dataReceiving = false;
                    }

                }


            }
            catch (IOException exception){

            }







    }

    public boolean sendData(String data){
        boolean isSent  = false;
        /** Must manage sequence when writing data using dataOutPutStream , in client end you should use this sequence to receive data**/

        try {
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            /** first int send for checking , second one is command of dataType , third is data which should be send
             *  flush the data so that it can reach the client point , will close the dataOutPutStream when close the client  **/
            dataOutputStream.write(1);
            dataOutputStream.writeUTF(Constant.STRING_TYPE_DATA);
            dataOutputStream.writeUTF(data);
            dataOutputStream.flush();
            Utils.log(data+" Send Successfull to "+ipAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  isSent ;
    }

    public void sendBitmap(Bitmap bitmap){


        try {
            /** Convert the bitmap into a byteArray so that we can send it using outPutStream **/
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream );
            byte[] outBuffer = stream.toByteArray();
            int length = outBuffer.length ;

            /** Must manage the sequence , first one is a int for checking ,
             * second one is the command for byteArrayType , thirdOne is length and fourth is byteArray **/

            dataOutputStream.write(1);
            dataOutputStream.writeUTF(Constant.BYTE_ARRAY_DATA);
            dataOutputStream.writeInt(length);
            dataOutputStream.write(outBuffer);
            dataOutputStream.flush();
            Utils.log("Bitmap byteArray send successfully ! to "+ipAddress);

        } catch (IOException e) {
            e.printStackTrace();

        }


    }





   public void closeClientSocket() throws IOException{

    /** Close Socket If Connection Is Lost **/
    Utils.log("Client Socket Closing");
       dataReceiving = false;
       bitmapReceiving = false;
       inputStream.close();
     if(!clientSocket.isClosed() && clientSocket!=null){
         clientSocket.close();
     }


   }



    /** Need to check the  is reachable , not doing anything right now **/
    private  class  ClientReachable extends AsyncTask<Socket, Void  , Boolean >{


        private Socket socket ;
        boolean connection = false ;
        @Override
        protected Boolean doInBackground(Socket... sockets) {


            try{
                this.socket = sockets[0];
                if(socket.getInetAddress().isReachable(500)){
                    connection = true;
                }

            }catch (IOException ex ){
                ex.printStackTrace();
            }

            return connection;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            isReachable = connection;
        }
    }




}

package com.dcastalia.serverwithservice.thread;

import com.dcastalia.serverwithservice.Utils.Constant;
import com.dcastalia.serverwithservice.Utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PT on 8/20/2017.
 */

public class ServerThread extends  Thread  {

    private boolean serviceRunning ;
    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    public static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];
    private BufferedReader bufferedIn  ;
    private String message ;
    private ClientMessageReceivedListener clientMessageReceivedListener ;

    public ServerThread( boolean serviceRunning , ClientMessageReceivedListener clientMessageReceivedListener){
        this.serviceRunning = serviceRunning ;
        this.clientMessageReceivedListener = clientMessageReceivedListener;
    }

    @Override
    public void run() {

        /** initialize serverSocket with a specific port , must check null before starting **/

            try {
                if(serverSocket==null) {
                    serverSocket = new ServerSocket(Constant.SERVER_MAIN_PORT);
                    Utils.log("Server Listening on Port " + Constant.SERVER_MAIN_PORT);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        while(serviceRunning){
            Utils.log("Server Running .. Waiting For Connection .");
            try {

                clientSocket = serverSocket.accept();
                Utils.log("New Client Connected "+clientSocket.getInetAddress().getHostAddress());
                /** Remove If the client Has Previous record searching through IP **/
                removeClient(clientSocket.getInetAddress().getHostAddress());
                // For client request




                /** Create Client Thread For All Individual Socket **/
                createThreadForClient(clientSocket);

                /** Print All Connected  Socket in the Logcat **/
                printAllClient();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }





    }

    private void createThreadForClient(Socket clientSocket) throws IOException {
        int i =0 ;
        for(i =0 ; i<maxClientsCount ; i++){
            if(threads[i]==null){
                (threads[i]= new ClientThread(clientSocket,threads,clientMessageReceivedListener)).start();
                Utils.log("New Thread Created For Client");
                threads[i].sendData(" Welcome To Teacher Student App. You are now connected with server");

                break;
            }
        }

        /** If max connection thread reach than sent a  message
         *  to client than not able to connected right now **/
        if(i==maxClientsCount){
            PrintStream os = new PrintStream(clientSocket.getOutputStream());
            os.println("Server To Busy. Try Later ( Max Connection Reached ) ");
            os.close();
            clientSocket.close();
        }
    }


    /** Make a public method so that this serverSocket can be close and restarted for later use **/

    public void closeServer() throws IOException {
        serviceRunning = false;
        closeAllClientThread();
        Utils.log("Server Close() Called ");

    }

    public void closeAllClientThread() throws IOException {
        synchronized (this){
            for(int i=0 ; i<threads.length ; i++){
               if(threads[i]!=null){
                   if(threads[i].clientSocket!=null && !threads[i].clientSocket.isClosed())
                   threads[i].closeClientSocket();
               }
            }
        }
    }



    public void removeClient(String ip){
        synchronized (this){
            for(int i=0 ; i<threads.length ; i++){
                if(threads[i]!=null){
                // Remove the same socket that are same ip address in previous

                    if(threads[i].clientSocket.getInetAddress().getHostAddress().equals(ip)){
                        threads[i] = null ;
                        Utils.log(ip+ " Has Record , So  Previous Thread Deleted");
                        break;
                    }

                }
            }
        }
    }
    public void removeAllClient(){
        synchronized (this){
            for(int i=0 ; i<threads.length;i++){
                threads[i]=null;
            }
        }
    }

    public String printAllClient() throws IOException{

        String clienlist = "";
        synchronized (this){
            for(int i=0 ; i<threads.length; i++ ){

                if (threads[i]!=null) {
                    Utils.log("Client Address : "+i +". "+ threads[i].clientSocket.getInetAddress().getHostAddress());
                    clienlist += "Client Address : "+i +". "+ threads[i].clientSocket.getInetAddress().getHostAddress()+"\n";
                }
            }
        }
        return clienlist ;
    }


    public void sendToAllClient(String s) {
        synchronized (this){
            for(int i =0 ; i<threads.length; i++){
                if(threads[i]!=null){
                    threads[i].sendData(s);
                }
            }
        }
    }

    public interface  ClientMessageReceivedListener{
         void  clientMessageReceived(String message);
    }
}

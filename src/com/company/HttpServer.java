package com.company;


import java.io.*;
import java.net.*;

public class HttpServer{

    private final static int PORT = 8080;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public HttpServer(){

    }

    public HttpServer(Socket socket){
        clientSocket = socket;
    }

    public void startServer() throws IOException {

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            Socket connection = serverSocket.accept();

            GETHandler test = new GETHandler(connection);
            Thread t = new Thread(test);
            t.start();

        }

    }

}

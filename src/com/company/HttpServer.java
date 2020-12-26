package com.company;


import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer{

    //private final static int PORT = 8080;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ExecutorService executorService;
    private int port;

    public HttpServer(){

    }

    public HttpServer(int port){
        this.port = port;
    }

    public HttpServer(Socket socket){
        clientSocket = socket;
    }

    public void startServer() throws IOException {

        try {
            serverSocket = new ServerSocket(port);
            executorService = Executors.newFixedThreadPool(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            Socket connection = serverSocket.accept();
            GETHandler test = new GETHandler(connection);
          executorService.submit(test);
           // Thread t = new Thread(test);
           // t.start();
        }
    }

}

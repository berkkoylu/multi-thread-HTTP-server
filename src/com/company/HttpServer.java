package com.company;


import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer{


    private final static int PORT = 8080;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Socket clientSocket;

    public HttpServer(){

    }

    public HttpServer(Socket socket){
        clientSocket = socket;
    }

    public void startServer() throws IOException {

        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            Socket connection = serverSocket.accept();

            GETHandler test = new GETHandler(connection);
            executorService.submit(test);
            //HttpServer main = new HttpServer(connection1);
            //Thread t = new Thread(connection1);
            //t.start();

        }

    }

//    @Override
//    public void run() {
//        try{
//            processRequest();
//        }catch (Exception e){
//            System.out.println(e);
//        }
//    }
//
//    private void processRequest() throws Exception{
//        InputStreamReader input_stream_reader = new InputStreamReader(clientSocket.getInputStream());
//        BufferedReader buffered_reader = new BufferedReader(input_stream_reader);
//        PrintWriter print_writer = new PrintWriter(clientSocket.getOutputStream());
//        System.out.println(buffered_reader.readLine());
//        System.out.println(Thread.currentThread().getId());
//        clientSocket.close();
//    }
}

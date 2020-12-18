package com.company;


import java.io.*;
import java.net.*;

public class HttpServer implements Runnable{

    private final static int PORT = 8080;
    private static ServerSocket socket;
    private Socket clientSocket;

    public HttpServer(Socket socket){
        clientSocket = socket;
    }

    public static void main(String[] args) throws IOException {

        try {
            socket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            Socket connection = socket.accept();
            HttpServer main = new HttpServer(connection);
            Thread t = new Thread(main);
            t.start();

        }

    }

    @Override
    public void run() {
        try{
            processRequest();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception{
        InputStreamReader input_stream_reader = new InputStreamReader(clientSocket.getInputStream());
        BufferedReader buffered_reader = new BufferedReader(input_stream_reader);
        PrintWriter print_writer = new PrintWriter(clientSocket.getOutputStream());
        System.out.println(buffered_reader.readLine());
        System.out.println(Thread.currentThread().getId());
    }
}

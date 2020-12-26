package com.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyServer implements Runnable{
    private final static int PORT = 8888;
    private ServerSocket proxySocket;
    private Socket clientSocket;
    private ExecutorService executorService;

    public ProxyServer(){

    }


    public void startServer() throws IOException{
        try {
            proxySocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true){
            Socket connection = proxySocket.accept();
            RequestHandler proxy = new RequestHandler(connection);
            executorService.submit(proxy);
//            Thread t = new Thread(proxy);
//            t.start();

        }
    }


    @Override
    public void run() {
        try{
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws IOException {
        InputStreamReader clientToProxy = new InputStreamReader(clientSocket.getInputStream());
        BufferedReader buffered_reader = new BufferedReader(clientToProxy);
        Socket serverSocket = new Socket("127.0.0.1",8080);
        OutputStream proxyToServer = serverSocket.getOutputStream();
        String request = buffered_reader.readLine();
        System.out.println(request);
        //String[] parameters = buffered_reader.readLine().split("\\s+");
        proxyToServer.write(request.getBytes(StandardCharsets.UTF_8));
        proxyToServer.flush();
       // proxyToServer.close();
        InputStream inFromServer = serverSocket.getInputStream();
        //InputStreamReader serverToProxy = new InputStreamReader(serverSocket.getInputStream());
        //BufferedReader fromServer = new BufferedReader(serverToProxy);
        //System.out.println(fromServer.readLine());
        OutputStream outToClient = clientSocket.getOutputStream();
        byte[] reply = new byte[4096];
        int bytes_read;
        try{
            while((bytes_read = inFromServer.read(reply)) != -1){
                outToClient.write(reply,0,bytes_read);
                outToClient.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //serverSocket.close();
        //int l = parameters[1].length();
        System.out.println("inside proxy");
        System.out.println(Thread.currentThread().getId());
        System.out.println("-----------");
    }
}

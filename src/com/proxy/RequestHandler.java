package com.proxy;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable{
    private Socket clientSocket;
    private InputStream clientToProxy;
    private OutputStream proxyToClient;
    InputStreamReader inputStreamReader;

    public RequestHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientToProxy = clientSocket.getInputStream();
        this.proxyToClient = clientSocket.getOutputStream();
        this.inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
    }

    @Override
    public void run() {
        try{
            gonzales();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws IOException {
        Socket webServer = new Socket("127.0.0.1",8080);
//        BufferedReader serverToProxy = new BufferedReader(new InputStreamReader(webServer.getInputStream()));
//        PrintWriter proxyToServer = new PrintWriter(webServer.getOutputStream());
//        BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
//
//        String request = fromClient.readLine();
//        String response = "";
//        proxyToServer.write(request);
//        proxyToServer.flush();
//        while((response = serverToProxy.readLine()) != null){
//            toClient.write(response);
//            toClient.flush();
//        }
//
//        fromClient.close();
//        toClient.close();
//        clientSocket.close();
        BufferedReader buffered_reader = new BufferedReader(inputStreamReader);
        OutputStream outputStream = webServer.getOutputStream();
        String request = buffered_reader.readLine();
        System.out.println(request);
        //String[] parameters = buffered_reader.readLine().split("\\s+");
        outputStream.write(request.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        String response = "";
        BufferedReader serverToProxy = new BufferedReader(new InputStreamReader(webServer.getInputStream()));
        PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
                while((response = serverToProxy.readLine()) != null){
            toClient.write(response);
            toClient.flush();
        }

        toClient.close();
        clientSocket.close();
        //InputStream clientToProxy = clientSocket.getInputStream();
//        OutputStream proxyToClient = clientSocket.getOutputStream();
//        InputStream serverToProxy = webServer.getInputStream();
//        OutputStream proxyToServer = webServer.getOutputStream();
//        InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());

//        BufferedReader buffered_reader = new BufferedReader(inputStreamReader);
////        Socket serverSocket = new Socket("127.0.0.1",8080);
////        OutputStream proxyToServer = serverSocket.getOutputStream();
//        String request = buffered_reader.readLine();
////        //String[] parameters = buffered_reader.readLine().split("\\s+");
//        proxyToServer.write(request.getBytes(StandardCharsets.UTF_8));
//        proxyToServer.flush();
//////        proxyToServer.close();
//////        InputStreamReader serverToProxy = new InputStreamReader(serverSocket.getInputStream());
//////        BufferedReader fromServer = new BufferedReader(serverToProxy);
//////        System.out.println(fromServer.readLine());
////        InputStream inFromServer = serverSocket.getInputStream();
////        OutputStream outToClient = clientSocket.getOutputStream();
//
//        byte[] reply = new byte[4096];
//        int bytes_read;
//        try{
//            while((bytes_read = serverToProxy.read(reply)) != -1){
//                proxyToClient.write(reply,0,bytes_read);
//                proxyToServer.flush();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        proxyToServer.close();
//        //webServer.close();
//        proxyToClient.close();
//        clientSocket.close();
        //serverSocket.close();
        //int l = parameters[1].length();
        System.out.println("inside proxy");
        System.out.println(Thread.currentThread().getId());
        System.out.println("-----------");
    }

    private void gonzales() throws IOException {
        Socket webServer;
        try {
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = clientSocket.getInputStream();
            final OutputStream outToClient = clientSocket.getOutputStream();
            Socket client = null, server = null;
            // connects a socket to the server
            try {
                server = new Socket("127.0.0.1",8080);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        outToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = server.getInputStream();
            final OutputStream outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytes_read;
                    try {
                        while ((bytes_read = inFromClient.read(request)) != -1) {
                            outToServer.write(request, 0, bytes_read);
                            outToServer.flush();
                            //TODO CREATE YOUR LOGIC HERE
                        }
                    } catch (IOException e) {
                    }
                    try {
                        outToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    System.out.println(new String(reply,StandardCharsets.UTF_8));
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                    //TODO CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

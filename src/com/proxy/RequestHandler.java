package com.proxy;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable {
    private Socket clientSocket;
    private InputStream clientToProxy;
    private OutputStream proxyToClient;
    private DataInputStream in;
    private DataOutputStream out;
    InputStreamReader inputStreamReader;

    public RequestHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientToProxy = clientSocket.getInputStream();
        this.proxyToClient = clientSocket.getOutputStream();
        this.in = new DataInputStream(clientSocket.getInputStream());
        this.out = new DataOutputStream(clientSocket.getOutputStream());
        this.inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequest() {
        try{
            String response = "";
            // get request from client
            BufferedReader b = new BufferedReader(new InputStreamReader(in));
            String request = b.readLine();
            System.out.println("request from client" + request);


            //send request to the web server
            Socket webServer = new Socket("127.0.0.1",8080);
            PrintWriter printWriter = new PrintWriter(webServer.getOutputStream());
            printWriter.println(request);
            printWriter.flush();

            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());


            //get response from web server

            BufferedReader fromServer = new BufferedReader(new InputStreamReader(webServer.getInputStream()));
            while((response = fromServer.readLine()) != null){
                System.out.println(response);
                toClient.println(response);
                toClient.flush();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}

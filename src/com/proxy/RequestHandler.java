package com.proxy;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

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
            String[] parameters = request.split("\\s+");
            int size = Integer.parseInt(parameters[1].substring(1));
            //send request to the web server


            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
            String filePath = size + "c" + ".html";

            File cachedFile = new File(filePath);
            cachedFile.createNewFile();
            FileWriter fileWriter = new FileWriter(cachedFile);
            PrintWriter cachedFileWriter = new PrintWriter(fileWriter);
            if(ProxyServer.checkCache(filePath)){
                returnCachedFile(filePath);
            }else{
                Socket webServer = new Socket("127.0.0.1",8080);
                PrintWriter printWriter = new PrintWriter(webServer.getOutputStream());
                printWriter.println(request);
                printWriter.flush();
                //get response from web server

                BufferedReader fromServer = new BufferedReader(new InputStreamReader(webServer.getInputStream()));
                while((response = fromServer.readLine()) != null){
                    System.out.println(response);
                    cachedFileWriter.println(response);
                    cachedFileWriter.flush();
                    toClient.println(response);
                    toClient.flush();
                }


                if(fromServer != null){

                }
                if(cachedFileWriter != null){
                    cachedFileWriter.close();
                }
                if(toClient != null){

                }
                fromServer.close();
                toClient.close();
                System.out.println("testasd");
                cachedFileWriter.close();
                System.out.println("testzxc");
                ProxyServer.addCache(filePath,cachedFile);
                webServer.close();
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

    private void returnCachedFile(String name) throws IOException {
        System.out.println("returned from cache");
        File cachedFile = ProxyServer.getCachedFile(name);
        PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(cachedFile));
            String line = reader.readLine();
            while(line != null){
                toClient.println(line);
                toClient.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            toClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}

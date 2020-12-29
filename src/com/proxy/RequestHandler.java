package com.proxy;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class RequestHandler implements Runnable {
    private Socket clientSocket;
    private int webServerPort;
    private InputStream clientToProxy;
    private OutputStream proxyToClient;
    private DataInputStream in;
    private DataOutputStream out;
    InputStreamReader inputStreamReader;


    public RequestHandler(Socket clientSocket, int webServerPort) throws IOException {
        this.clientSocket = clientSocket;
        this.clientToProxy = clientSocket.getInputStream();
        this.proxyToClient = clientSocket.getOutputStream();
        this.webServerPort = webServerPort;
        this.in = new DataInputStream(clientSocket.getInputStream());
        //this.out = new DataOutputStream(clientSocket.getOutputStream());
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
            String test = analyzeURL(request);
//            URI uri = new URI(request);
//            String host = uri.getHost();
//            String query = uri.getQuery();
//            request = java.net.URLDecoder.decode(request,"UTF-8");
            if("null".equals(request)){
                return;
            }
            System.out.println("request from client" + test);
            String[] query = test.split("\\s+");
            //String[] parameters = query[0].split("/");
            String methodType = query[0];
            int size = 0;
            //send request to the web server
            try{
                size = Integer.parseInt(query[1].substring(1));
                if(size > 9999){
                    requestTooLong();
                }
               // size = Integer.parseInt(parameters[1].substring(15));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            String filePath = size + "c" + ".html";


            if(ProxyServer.checkCache(filePath)){
                returnCachedFile(filePath);
            }else{

                File cachedFile = new File(filePath);
                cachedFile.createNewFile();
                FileWriter fileWriter = new FileWriter(cachedFile);
                PrintWriter cachedFileWriter = new PrintWriter(fileWriter);
                Socket webServer = null;
                try{
                     webServer = new Socket(InetAddress.getLocalHost().getHostName(),webServerPort);
                } catch (IOException e) {
                    e.printStackTrace();
                    notFound();
                }
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
                PrintWriter printWriter = new PrintWriter(webServer.getOutputStream());
                printWriter.println(test);
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
                    fromServer.close();
                }
                if(cachedFileWriter != null){
                    cachedFileWriter.close();
                }
                if(toClient != null){
                    toClient.close();
                }


                System.out.println("testasd");
                //cachedFileWriter.close();
                System.out.println("testzxc");
                if(!("0c.html".equals(filePath)) && ("GET".equals(methodType))){
                    ProxyServer.addCache(filePath,cachedFile);
                }
                webServer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            clientSocket.close();
//            in.close();
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void returnCachedFile(String name) throws IOException {
        System.out.println("returned from cache");
        File cachedFile = ProxyServer.getCachedFile(name);
        OutputStream outToClient = clientSocket.getOutputStream();
        //PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
        Path filePath = Paths.get(name);
        InputStream f = new FileInputStream(cachedFile);
        Long file_size = cachedFile.length();
        try {
            outToClient.write(Files.readAllBytes(filePath));
            outToClient.flush();
//            BufferedReader reader = new BufferedReader(new FileReader(cachedFile));
//            String line = reader.readLine();
//            while(line != null){
//                toClient.println(line);
//                toClient.flush();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //toClient.close();
            in.close();
            outToClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestTooLong() throws IOException {
        OutputStream outToClient = clientSocket.getOutputStream();
        String status = "HTTP/1.1 414 Request-URI Too Long\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length: 0\r\n\r\n";
        try{
            outToClient.write(status.getBytes(StandardCharsets.UTF_8));
            outToClient.write(server.getBytes(StandardCharsets.UTF_8));
            outToClient.write(content_type.getBytes(StandardCharsets.UTF_8));
            outToClient.write(content_length.getBytes(StandardCharsets.UTF_8));
            outToClient.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            in.close();
            outToClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notFound() throws IOException {
        OutputStream outToClient = clientSocket.getOutputStream();
        String status = "HTTP/1.1 404 Not Found\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length: 0\r\n\r\n";
        try{
            outToClient.write(status.getBytes(StandardCharsets.UTF_8));
            outToClient.write(server.getBytes(StandardCharsets.UTF_8));
            outToClient.write(content_type.getBytes(StandardCharsets.UTF_8));
            outToClient.write(content_length.getBytes(StandardCharsets.UTF_8));
            outToClient.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            in.close();
            outToClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String analyzeURL(String request) {
        String decode = "";
        String[] parameters = request.split("\\s+");

        String[] test = parameters[1].split("/");
        decode += parameters[0] + " /" + test[3] + " " + parameters[2] + "\n";
        decode += "Host:" + test[0] + test[2];
        return decode;
    }



}

package com.proxy;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class RequestHandler implements Runnable {
    private String ifModifiedSinceHeader;
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
            String line = "";
            String response = "";
            // get request from client
            BufferedReader b = new BufferedReader(new InputStreamReader(in));
            String request = b.readLine();
            request = analyzeURL(request);
//            String test = analyzeURL(request);
            String test = test(b);
//            URI uri = new URI(request);
//            String host = uri.getHost();
//            String query = uri.getQuery();
//            request = java.net.URLDecoder.decode(request,"UTF-8");
//            if("null".equals(request)){
//                return;
//            }
            System.out.println("request from client" + test);
            String[] query = request.split("\\s+");
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

            boolean ifModified = checkIfModifiedSinceHeader(test);
            String filePath = size + "c" + ".html";
            String date = "";
            boolean needToServer = false;
            if(ifModified){
                System.out.println(ifModifiedSinceHeader);
                date = getDate(filePath);
                needToServer = compareDates(date,ifModifiedSinceHeader);


            }

            //needtoservre --> true
            //needtoserver --> false
            // ifModified && !needToServer
            if(ProxyServer.checkCache(filePath)){
                // server'a gidip gelicek
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
                printWriter.println(request);
                printWriter.println(test);
//                printWriter.flush();
//                while (!(line = b.readLine()).equals("")) {
//                    System.out.println(line);
//                    printWriter.println(line);
//                }
//                }
//                do {
//
//                    printWriter.flush();
//                } while (!(line = b.readLine()).equals(""));
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
    }

    private void returnCachedFile(String name) throws IOException {
        System.out.println("returned from cache");
        File cachedFile = ProxyServer.getCachedFile(name);
        OutputStream outToClient = clientSocket.getOutputStream();
        Path filePath = Paths.get(name);
        try {
            outToClient.write(Files.readAllBytes(filePath));
            outToClient.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
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
        decode += parameters[0] + " /" + test[3] + " " + parameters[2];
        //decode += "Host:" + test[0] + test[2];
        return decode;
    }

    private String test(BufferedReader b) throws IOException {
        String decode = "";
        String line = "";
        while(!(line = b.readLine()).equals("")){
            if(line.contains("If-Modified-Since:")){
                ifModifiedSinceHeader = line.substring(18);
            }
            decode += line;
            decode += "\n";
        }
        decode += "\r\n";
        return decode;
    }

    private boolean checkIfModifiedSinceHeader(String request){
        return request.contains("If-Modified-Since:");
    }

    private String getDate(String filePath){
        String date = "";
        File file = new File(filePath);
        Scanner in = null;
        try{
            in = new Scanner(file);
            while(in.hasNext()){
                String line = in.nextLine();
                if(line.contains("Date:")){
                    return line.substring(6);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return date;
    }

    private boolean compareDates(String file, String request) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        Date date1 = dateFormat.parse(file);
        Date date2 = dateFormat.parse(file);
        if(date2.compareTo(date1) <= 0){
            return true;
        }else{
            return false;
        }
    }


}

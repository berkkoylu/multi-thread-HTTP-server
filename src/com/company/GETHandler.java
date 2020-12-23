package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class GETHandler implements Runnable{

    public static int counter = 0 ;

    String badRequestHTML = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "  <head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>501</title>\n" +
            "  </head>\n" +
            "    <body>\n" +
            "        <p>HTTP/1.1 Server</p>\n" +
            "        <p>501 | Not Implemented</p>\n" +
            "    </body>\n" +
            "</html>";

    private static final String crlf= "\r\n";

    private static final int BAD_REQUEST= 400;
    private static final int NOT_IMPLEMENTED = 501;
    InputStreamReader inputStreamReader;
    DataOutputStream dataOutputStream;
    Socket socket;

    public GETHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStreamReader = new InputStreamReader(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
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
        try{
//
//            if(counter == 0 ){
//                System.out.println(counter + " counter");
//                counter++;
//                Thread.sleep(5000);
//
//            }
//
//            counter = counter + counter;
//            System.out.println(counter + " counter");

            //InputStreamReader input_stream_reader = new InputStreamReader(socket.getInputStream());
            BufferedReader buffered_reader = new BufferedReader(inputStreamReader);
           // DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

            String[] parameters = buffered_reader.readLine().split("\\s+");
            PrintWriter print_writer = new PrintWriter(socket.getOutputStream());
            if(!("GET".equals(parameters[0]))){
                //print_writer.write("HTTP/1.1 400 Bad Request \r\n");
                ///writer.writeBytes("HTTP/1.1 " + 501 + "NOT_IMPLEMENTED" + "\n");
                //dataOutputStream.close();
                not_implemented();
            }else if(!(parameters[1].matches("[0-9]+"))){
                bad_request();
            }
            not_implemented();
            System.out.println(buffered_reader.readLine());
            System.out.println(Thread.currentThread().getId());
            socket.close();
        } catch (  IOException e) {
            e.printStackTrace();
        }
    }

    private void not_implemented() throws IOException {
        String status = "HTTP/1.1 501 NOT_IMPLEMENTED\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length:"+badRequestHTML.getBytes().length+"\r\n";
        String header = status + server + content_type + content_length +  crlf + badRequestHTML + crlf + crlf;
        dataOutputStream.writeBytes(header);

    }

    private void bad_request() throws  IOException {
        String status = "HTTP/1.1 400 BAD_REQUEST\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length: 0\n\n";
        String header = status + server + content_type + content_length;
        dataOutputStream.writeBytes(header);

    }
}

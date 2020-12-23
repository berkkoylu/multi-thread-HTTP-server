package com.company;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class GETHandler implements Runnable{
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
            }else if(!(parameters[1].substring(1, parameters.length - 1).matches("[0-9]+"))){
                bad_request();
            }else{
                ok();
            }
            System.out.println(buffered_reader.readLine());
            System.out.println(Thread.currentThread().getId());
            socket.close();
        }finally {

        }
    }

    private void not_implemented() throws IOException {
        String status = "HTTP/1.1 501 NOT_IMPLEMENTED\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html; charset=UTF-8\r\n";
        String content_length = "Content-Length: 0\r\n\r\n";
        String body = "501 NOT IMPLEMENTED";
        String header = status + server + content_type + content_length;
        dataOutputStream.writeBytes(header);
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    private void bad_request() throws  IOException {
        String status = "HTTP/1.1 400 BAD_REQUEST\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length: 0\r\n\r\n";
        String body = "400 BAD REQUEST";
        String header = status + server + content_type + content_length;
        dataOutputStream.writeBytes(header);
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    private void ok() throws  IOException{
        String status = "HTTP/1.1 200 OK\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length: 10\r\n\r\n";
        String body = "test";
        String header = status + server + content_type + content_length + body;
        dataOutputStream.writeBytes(header);
        dataOutputStream.flush();
        dataOutputStream.close();
    }
}

package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class GETHandler implements Runnable{
    private static final int BAD_REQUEST= 400;
    private static final int NOT_IMPLEMENTED = 501;
    InputStreamReader inputStreamReader;
    DataOutputStream dataOutputStream;
    PrintWriter printWriter;
    PrintStream printStream;
    Socket socket;

    public GETHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStreamReader = new InputStreamReader(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.printWriter = new PrintWriter(socket.getOutputStream());
        this.printStream = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
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
            int l = parameters[1].length();
            if(!("GET".equals(parameters[0]))){
                //print_writer.write("HTTP/1.1 400 Bad Request \r\n");
                ///writer.writeBytes("HTTP/1.1 " + 501 + "NOT_IMPLEMENTED" + "\n");
                //dataOutputStream.close();
                not_implemented();
            }else if(!(parameters[1].substring(1, l - 1).matches("[0-9]+"))){
                bad_request();
            }else{
                int size = Integer.parseInt(parameters[1].substring(1, l));
                File file = createFile(size);
                ok(file, size);
            }
            //System.out.println(buffered_reader.readLine());
            System.out.println(Thread.currentThread().getId());
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

    private void ok(File file, int size) throws  IOException{
        OutputStream clientOutput = socket.getOutputStream();
        String file_path = size + ".html";
        Path filePath = Paths.get(file_path);
        InputStream f = new FileInputStream(file);
        Long file_size = file.length();
        String status = "HTTP/1.1 200 OK\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length: " + file_size +"\r\n\r\n";
        clientOutput.write(status.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(server.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(content_type.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(content_length.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(Files.readAllBytes(filePath));
        clientOutput.flush();
        socket.close();

//        String header = status + server + content_type + content_length;
//        File response_body = new File("");
//
//        printWriter.write(header);
//        printWriter.flush();
//        printWriter.close();
        //dataOutputStream.writeBytes(header);
        //dataOutputStream.writeBytes(body);
        //dataOutputStream.flush();
        //dataOutputStream.close();
        ////////////////////////////
//        printStream.print(status);
//        printStream.print(server);
//        printStream.print(content_type);
//        printStream.print(content_length);
//        byte[] line = new byte[4096];
//        int n;
//        while ((n=f.read(line)) > 0){
//            printStream.write(line,0,n);
//        }
//        printStream.close();
    }

    private File createFile(int size) throws IOException {
        File file = new File(Integer.toString(size) + ".html");
        file.createNewFile();

        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        raf.setLength(size);
        raf.close();
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("<html>");
        printWriter.println("<head>");
        printWriter.println("<title>I am " + size + " bytes long</title>");
        printWriter.println("</head>");
        printWriter.println("<body>");
        for (int i = 0; i < size - 80; i++) {
            printWriter.print("a");
        }
        printWriter.println("</body>");
        printWriter.println("</html>");


        printWriter.close();
        return file;
    }
}

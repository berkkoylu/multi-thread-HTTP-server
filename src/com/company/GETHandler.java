package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetAddress;
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
//        String url = InetAddress.getLocalHost().getHostAddress();
//        this.socket = new Socket(url,8888);
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
            BufferedReader buffered_reader = new BufferedReader(inputStreamReader);


            String request = buffered_reader.readLine();
            System.out.println(request);
            String[] parameters = request.split("\\s+");
            int l = parameters[1].length();
            //System.out.println(buffered_reader.readLine());
            System.out.println("test0");
            if(!("GET".equals(parameters[0]))){
                System.out.println("test1");
                not_implemented();
            }else if(!(parameters[1].substring(1, l - 1).matches("[0-9]+"))){
                System.out.println("test2");
                bad_request();
            }else{
                System.out.println("test3");
                int size = Integer.parseInt(parameters[1].substring(1, l));
                File file = createFile(size);
                ok(file, size);
            }
            System.out.println("inside web server");
            System.out.println(Thread.currentThread().getId());
            System.out.println("----------");
        }finally {
        }
    }

    private void not_implemented() throws IOException {
        String status = "HTTP/1.1 501 NOT_IMPLEMENTED\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html; charset=UTF-8\r\n";
        String content_length = "Content-Length: 0\r\n\r\n";
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
        String header = status + server + content_type + content_length;
        dataOutputStream.writeBytes(header);
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    private void ok(File file, int size) throws  IOException{
        OutputStream clientOutput = socket.getOutputStream();
        try{

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
        clientOutput.write("null".getBytes(StandardCharsets.UTF_8));
        clientOutput.flush();
        //clientOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //clientOutput.close();
        }
        //socket.close();
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

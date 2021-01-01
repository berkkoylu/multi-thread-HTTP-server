package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class GETHandler implements Runnable{
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

    private void processRequest() throws IOException{
        try{
            BufferedReader buffered_reader = new BufferedReader(inputStreamReader);

            String line = null;
            String request = buffered_reader.readLine();
            System.out.println(request);
//            for (int i = 0; i < 5; i++) {
//                String test = buffered_reader.readLine();
//            }
            while (!(line = buffered_reader.readLine()).equals("")) {
                System.out.println(line);
                printWriter.println(line);
            }
//            do {
//                System.out.println(line);
//            } while (!(line = buffered_reader.readLine()).equals(""));
            //System.out.println(request);
            String[] parameters = request.split("\\s+");
            int l = parameters[1].length();
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
                if(size < 100 || size > 20000){
                    bad_request();
                }else{
                    ok(size);
                }

            }
            System.out.println("inside web server");
            System.out.println(Thread.currentThread().getId());
            System.out.println("----------");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
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

    private void ok(int size) throws  IOException{
        String date = "Date: ";
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        date += getTime();
        date += "\r\n";
        OutputStream clientOutput = socket.getOutputStream();
        try{
        String body = "";
        String html_start = "<html>\n" +
                "<head>\n" +
                "<title> I am " + size + " bytes long</title>\n" +
                "</head>\n" +
                "<body>\n";
        String html_end = "</body>\n" +
                "</html>";
        String status = "HTTP/1.1 200 OK\r\n";
        String server = "Server: HTTP Server/1.1\r\n";
        String content_type = "Content-Type: text/html\r\n";
        String content_length = "Content-Length: " + size +"\r\n\r\n";
        clientOutput.write(status.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(server.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(date.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(content_type.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(content_length.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(html_start.getBytes(StandardCharsets.UTF_8));
        for(int i = 0; i < size - 80; i++){
            body += "a";
        }
        clientOutput.write(body.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(html_end.getBytes(StandardCharsets.UTF_8));
        clientOutput.flush();
        socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

}

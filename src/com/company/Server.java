package com.company;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException {


        if(args.length != 1){
            System.out.println("Usage: Server.Class {port-number}");
        }
        System.out.println(args[0]);
        HttpServer server = new HttpServer(Integer.parseInt(args[0]));
        server.startServer();

    }
}

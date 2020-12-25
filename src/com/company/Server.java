package com.company;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException {


        if(args.length != 1){
            System.out.println("Usage: Server.Class {port-number}");
        }
        HttpServer server = new HttpServer();
        server.startServer();

    }
}

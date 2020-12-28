package com.proxy;

import com.company.HttpServer;

import java.io.IOException;

public class ProxyMain {
    public static void main(String[] args) throws IOException {

        if(args.length != 1){
            System.out.println("Usage: Server.Class {port-number}");
        }
        System.out.println(args[0]);
        ProxyServer server = new ProxyServer(Integer.parseInt(args[0]));
        server.startServer();

    }
}

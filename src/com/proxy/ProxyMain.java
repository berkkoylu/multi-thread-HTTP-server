package com.proxy;

import com.company.HttpServer;

import java.io.IOException;

public class ProxyMain {
    public static void main(String[] args) throws IOException {

        ProxyServer server = new ProxyServer();
        server.startServer();

    }
}

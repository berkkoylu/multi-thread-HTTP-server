package com.company.deneme;

import java.io.IOException;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

public class HttpServerDeneme {
    private final static int PORT = 8080;
    public static void main(String[] args) throws IOException {


        LOGGER.info("Server starting...");

        ServerListener serverListener = new ServerListener(PORT);
        serverListener.start();


    }

}

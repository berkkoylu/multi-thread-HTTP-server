package com.company.deneme;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Thread{
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListener.class);

    private ServerSocket serverSocket;

    public ServerListener(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {

        try {
            while ( serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                HttpConnectionWorker workerThread = new HttpConnectionWorker(socket);
                workerThread.start();

            }

        } catch (IOException e) {
            LOGGER.error("Problem with setting socket", e);
        }

    }


}

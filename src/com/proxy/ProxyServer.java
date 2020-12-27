package com.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyServer{
    private final static int PORT = 8888;
    private ServerSocket proxySocket;
    private Socket clientSocket;
    private ExecutorService executorService;
    static HashMap<String, File> cache;
    public ProxyServer(){

    }


    public void startServer() throws IOException{
        try {
            proxySocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(5);
            cache = new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true){
            Socket connection = proxySocket.accept();
            RequestHandler proxy = new RequestHandler(connection);
         //   executorService.submit(proxy);
            Thread t = new Thread(proxy);
            t.start();

        }
    }

    public static void addCache(String name, File file){
        cache.put(name,file);
    }

    public static boolean checkCache(String name){
        return cache.containsKey(name);
    }

    public static File getCachedFile(String name){
        return cache.get(name);
    }



}

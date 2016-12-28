package org.sai.talks.server;

import org.sai.talks.util.AppUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by saipkri on 26/12/16.
 */
// PORT 7072
public class MultiThreadedBlockingThreadPoolServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 7072));
        ExecutorService threadpool = Executors.newFixedThreadPool(100);

        System.out.println("Waiting for someone to connect with me .....");
        while (true) {
            Socket clientSocket = serverSocket.accept(); // This is blocking until a client establishes a connection.

            Runnable requestHandler = () -> {
                try {
                    while (true) {
                        clientSocket.getOutputStream().write(AppUtils.doBusinessLogic(clientSocket.getInputStream(), "MultiThreadedBlockingThreadPoolServer").getBytes());
                    }
                } catch (IOException ignore) {
                }
            };
            threadpool.submit(requestHandler);
        }
    }
}

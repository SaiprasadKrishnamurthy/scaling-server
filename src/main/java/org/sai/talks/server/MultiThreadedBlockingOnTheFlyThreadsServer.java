package org.sai.talks.server;

import org.sai.talks.util.AppUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by saipkri on 26/12/16.
 */
// PORT 7071
public class MultiThreadedBlockingOnTheFlyThreadsServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 7071));

        System.out.println("Waiting for someone to connect with me .....");
        while (true) {
            Socket clientSocket = serverSocket.accept(); // This is blocking until a client establishes a connection.
            System.out.println("Hey! I'm connected to: " + clientSocket);

            new Thread(() -> {
                try {
                    clientSocket.getOutputStream().write(AppUtils.doBusinessLogic(clientSocket.getInputStream()).getBytes());
                } catch (IOException ignore) {
                }
            }).start();
        }
    }
}

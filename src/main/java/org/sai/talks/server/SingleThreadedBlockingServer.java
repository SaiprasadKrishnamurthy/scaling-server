package org.sai.talks.server;

import org.sai.talks.util.AppUtils;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by saipkri on 26/12/16.
 */
// PORT 7070
public class SingleThreadedBlockingServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 7070));

        System.out.println("Waiting for someone to connect with me .....");
        Socket clientSocket = serverSocket.accept(); // This is blocking until a client establishes a connection.
        System.out.println("Hey! I'm connected to: " + clientSocket);
        while (clientSocket.isConnected()) {
            clientSocket.getOutputStream().write(AppUtils.doBusinessLogic(clientSocket.getInputStream(), "SingleThreadedBlockingServer").getBytes());
        }
    }
}

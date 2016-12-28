package org.sai.talks.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by saipkri on 26/12/16.
 */
public class ClientsSimulation {

    private static int port = 7074;

    public static void killer1() throws Exception {
        for (int i = 0; i < 3000; i++) {
            new Socket("localhost", port);
        }
    }

    public static void main(String[] args) throws Exception {
//        killer2();
//        killer1();
        killer3();

        Thread.sleep(1000000000);
    }

    private static void killer3() throws Exception {
        InetSocketAddress addr = new InetSocketAddress("localhost", port);
        ObjectMapper mapper = new ObjectMapper();
        for (int j = 0; j < 3000; j++) {
            SocketChannel client = SocketChannel.open(addr);
            client.configureBlocking(false);
            Map<String, Object> json = new HashMap<>();
            json.put("message", "I met Randy the other day and he's a bloke");
            json.put("timestamp", System.currentTimeMillis());
            String req = mapper.writeValueAsString(json);
            byte[] message = req.getBytes();
            System.out.println(message.length);
            ByteBuffer buffer = ByteBuffer.wrap(message);
            client.write(buffer);
            buffer.clear();
        }
    }


    private static void killer2() throws IOException {
        ExecutorService requestSenderThreadPool = Executors.newFixedThreadPool(10);
        ExecutorService responseReaderThreadPool = Executors.newFixedThreadPool(50);
        AtomicInteger c = new AtomicInteger(1);
        ObjectMapper mapper = new ObjectMapper();

        for (int j = 0; j < 500; j++) {
            requestSenderThreadPool.submit(() -> {
                try {
                    final Socket socket = new Socket("localhost", port);
                    // Send a request.
                    Map<String, Object> json = new HashMap<>();
                    json.put("message", "I met Randy the other day and he's a bloke");
                    json.put("timestamp", System.currentTimeMillis());
                    String req = mapper.writeValueAsString(json);
                    socket.getOutputStream().write(req.getBytes());
                    Runnable responseReader = () -> {
                        byte[] buff = new byte[150];
                        // Read the response.
                        try {
                            socket.getInputStream().read(buff);
                            socket.close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        for (int i = 0; i < buff.length && buff[i] > 0; i++) {
                            System.out.print((char) buff[i]);
                        }
                        System.out.println(" [" + c.getAndAdd(1) + "]");
                    };
                    //responseReaderThreadPool.submit(responseReader);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            });
        }
    }
}

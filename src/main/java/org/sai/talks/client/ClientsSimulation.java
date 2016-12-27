package org.sai.talks.client;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
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
        killer2();
//        killer1();

        Thread.sleep(1000000000);
    }

    private static void killer2() throws IOException {
        ExecutorService responseReaderThreadPool = Executors.newFixedThreadPool(10);
        AtomicInteger c = new AtomicInteger(1);

        for (int j = 0; j < 3000; j++) {
            final Socket socket = new Socket("localhost", port);
            // Send a request.
            socket.getOutputStream().write("Cruel World".getBytes());
            Runnable responseReader = () -> {
                byte[] buff = new byte[80];
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
            responseReaderThreadPool.submit(responseReader);
        }
    }
}

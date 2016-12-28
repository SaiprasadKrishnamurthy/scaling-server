package org.sai.talks.server;

import org.sai.talks.util.AppUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by saipkri on 26/12/16.
 */
// PORT 7074
public class MultiThreadedNonBlockingServer {
    private static Map<SocketChannel, byte[]> dataMap = new ConcurrentHashMap<>();
    private static ExecutorService writeWorkerPool = Executors.newFixedThreadPool(100);


    public static void main(String[] args) throws Exception {
        // Setup the server socket channel and the selector to listen to connections.
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress("localhost", 7074));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        AtomicInteger clientsCount = new AtomicInteger(0);
        int index = 0;


        // Listen to events in this infinite event loop.
        while (true) {
            selector.select(0);
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                // remove the key so that we don't process this OPERATION again.
                keys.remove();

                // key could be invalid if for example, the client closed the connection.
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);

                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    System.out.println("Connection : "+index);
                    index++;
                    key.interestOps(SelectionKey.OP_READ);
                    writeWorkerPool.submit(() -> write(key));
                }
            }
        }
    }

    private static void write(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            byte[] data = dataMap.get(channel);
            dataMap.remove(channel);
            channel.write(ByteBuffer.wrap(AppUtils.doBusinessLogic(new ByteArrayInputStream(data), "MultiThreadedNonBlockingServer").getBytes()));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void read(final SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(1048);
            readBuffer.clear();
            int read;
            try {
                read = channel.read(readBuffer);
            } catch (IOException e) {
                key.cancel();
                channel.close();
                return;
            }
            if (read == -1) {
                channel.close();
                key.cancel();
                return;
            }
            readBuffer.flip();
            byte[] data = new byte[200];
            readBuffer.get(data, 0, read);
            dataMap.put(channel, data);
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}

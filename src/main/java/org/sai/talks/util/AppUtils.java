package org.sai.talks.util;

import org.HdrHistogram.Histogram;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static java.util.stream.Collectors.joining;

/**
 * Created by saipkri on 26/12/16.
 */
public final class AppUtils {
    private AppUtils() {
    }

    public static String doBusinessLogic(final InputStream in) {
        try {
            byte[] buff = new byte[80];
            System.out.println("\t Im connected ");
            in.read(buff); // Blocking
            System.out.println(" after ");

            String response = new String(buff).chars()
                    .mapToObj(c -> (char) c)
                    .filter(i -> i > 0 && i < 128) // ASCII only
                    .map(c -> Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c)) // Toggle case
                    .map(Object::toString)
                    .collect(joining(""));
            Thread.sleep(100); // Simulate a delay.
            return response;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

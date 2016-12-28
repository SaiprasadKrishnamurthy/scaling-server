package org.sai.talks.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Created by saipkri on 26/12/16.
 */
public final class AppUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
    private static final RestTemplate restTemplate = new RestTemplate();

    private AppUtils() {
    }

    public static String doBusinessLogic(final InputStream in, final String serverId) {
        try {
            byte[] buff = new byte[150];
            in.read(buff); // Blocking
            String requestJson = new String(buff).chars()
                    .mapToObj(c -> (char) c)
                    .filter(i -> i > 0 && i < 129) // ASCII only
                    .map(Object::toString)
                    .collect(joining(""));
            Map json = mapper.readValue(requestJson.getBytes(), Map.class);
            json.put("serverId", serverId);
            long start = (long) json.get("timestamp");
            json.put("responseTime", (System.currentTimeMillis() - start));
//            System.out.println(" \t "+json.get("responseTime") + ", "+Thread.currentThread().getId());
            json.put("dateTime", format.format(new Date(start)));
            indexData(json);
//            Thread.sleep(20); // Simulate a delay.

            return json.get("message").toString().chars()
                    .mapToObj(c -> (char) c)
                    .map(c -> Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c))
                    .map(c -> c.toString())
                    .collect(joining());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private static void indexData(final Map json) throws JsonProcessingException {
        try {
            restTemplate.postForObject("http://localhost:9200/servers_benchmarking/servers_benchmarking", mapper.writeValueAsString(json), Map.class);
        }catch(Exception ex){

        }
    }
}

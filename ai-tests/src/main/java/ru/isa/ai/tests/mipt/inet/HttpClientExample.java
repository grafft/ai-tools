package ru.isa.ai.tests.mipt.inet;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Author: Aleksandr Panov
 * Date: 11.04.2014
 * Time: 11:52
 */
public class HttpClientExample {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://www.google.com/search?hl=en&q=httpclient&btnG=Google+Search&aq=f&oq=");
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();

            BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
            String input;
            while ((input = in.readLine()) != null)
                System.out.println(input);

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }
}

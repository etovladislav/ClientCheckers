package ru.kpfu.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Created by etovladislav on 05.06.16.
 */
public class Request {

    public String post(String url, List<NameValuePair> data) throws IOException, RequestError {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(Properties.SERVER_URL + url);
        post.setHeader("User-Agent", USER_AGENT);
        List<NameValuePair> urlParameters = data;
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = null;
        response = client.execute(post);
        BufferedReader rd = null;
        rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        Integer responseStatus = response.getStatusLine().getStatusCode();
        if (responseStatus != 200) {
            throw new RequestError(result.toString());
        }
        return result.toString();
    }

    public String get(String url) throws IOException, RequestError {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        Integer responseStatus = response.getStatusLine().getStatusCode();
        if (responseStatus != 200) {
            throw new RequestError(result.toString());
        }
        return result.toString();
    }
}

package com.tappx.technicaltest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

public class Tappx {

    private static final String key= "pub1234-android-1234";
    private static final String url= "http://test-ssp.tappx.net/ssp/req.php?";

    private Integer timeout;
    private JSONObject jsonObject;
    private String filePath;
    private Integer statusCode;
    private String advert;
    
    public Tappx(Integer timeout, String filePath) {
        this.timeout = timeout;
        this.filePath = filePath;
    }

    public String calculateURL() throws JSONException {
        jsonObject = new JSONObject(fileToString());
    
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("key", key);
        requestParams.put("sz", "320x480");
        requestParams.put("os", jsonObject.getJSONObject("device").getString("os"));
        requestParams.put("ip", jsonObject.getJSONObject("device").getString("ip"));
        if(jsonObject.has("app")) requestParams.put("source", "app");
        else requestParams.put("source", "web");
        requestParams.put("ab", jsonObject.getJSONObject("app").getString("bundle"));
        requestParams.put("aid", "19a6c729-1e27-e936-84c1-122b2a9bbc8c");
        requestParams.put("mraid", "2");
        requestParams.put("cb", "23482834829");
        requestParams.put("ua", jsonObject.getJSONObject("device").getString("ua"));
        requestParams.put("timeout", timeout.toString());
        requestParams.put("lat", jsonObject.getJSONObject("device").getJSONObject("geo").getNumber("lat").toString());
        requestParams.put("lon", jsonObject.getJSONObject("device").getJSONObject("geo").getNumber("lon").toString());

        String encodedURL = requestParams.keySet().stream().map(key -> key + "=" + encodeValue(requestParams.get(key))).collect(Collectors.joining("&", url, ""));
        return encodedURL;
    }
    
    public void sendHttpRequest(String urlSend) {
        URL finalURL;
        try {
            finalURL = new URL(urlSend);
            HttpURLConnection connection = (HttpURLConnection) finalURL.openConnection();
            connection.setRequestMethod("GET");
            
            statusCode = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            advert = content.toString();
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (ProtocolException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getAdvert() {
        return advert;
    }

    private String fileToString() {
        try {
            InputStream is = new FileInputStream(filePath);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            String fileAsString = sb.toString();
            buf.close();
            return fileAsString;

        } catch(Exception e){
            System.out.println("Error");
            return e.getMessage();
        }

    } 

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
    
    public Boolean checkFileExists() {
        File file = new File(filePath);
        return file.exists();
    }
}

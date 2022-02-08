package com.tappx.technicaltest;

import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Launcher {

    @RequestMapping(value="/encodeURL", method = RequestMethod.POST, consumes = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
    public String encodeURL(@RequestBody String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "Error while encoding URL";
        }
    }

    @RequestMapping(value = "/getAdvert", method = RequestMethod.GET)
    public ResponseEntity<?> getAdvert(@RequestParam String pathFile, @RequestParam Integer timeout) {
        Tappx tappx = new Tappx(timeout, pathFile);
        String urlEncoded;
        if(!tappx.checkFileExists()) return new ResponseEntity<>(generateResponse("Error", "The file does not exist"), HttpStatus.BAD_REQUEST);
        try {
            urlEncoded = tappx.calculateURL();
            tappx.sendHttpRequest(urlEncoded);
    
            Integer code = tappx.getStatusCode();
            String advert = tappx.getAdvert();
            
    
            switch (code) {
                case 200: {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", "text/html; charset=utf-8");
                    return new ResponseEntity<>(advert, headers, HttpStatus.OK);
                }
                case 204: return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                default: return new ResponseEntity<>(generateResponse("Error", "Error while processing advert"), HttpStatus.BAD_REQUEST);
            }
        } catch (JSONException e) {
            return new ResponseEntity<>(generateResponse("Error", "The file does not have JSON format"), HttpStatus.BAD_REQUEST);
        }
        
        
    }

    private Map<String, String> generateResponse(String key, String value) {
        Map<String, String> response = new HashMap<>();
        response.put(key, value);
        return response;
    }
}

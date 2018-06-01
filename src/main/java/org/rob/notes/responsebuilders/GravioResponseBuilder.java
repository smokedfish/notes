package org.rob.notes.responsebuilders;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

public class GravioResponseBuilder implements ResponseBuilder {
    private final RestOperations restTemplate;

    public GravioResponseBuilder(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public byte[] response(File doc) throws Exception {
        String url = "http://g.gravizo.com/svg?{svg}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("image", "svg+xml"));
        headers.set(HttpHeaders.USER_AGENT, "spring resttemplate");
        headers.setAcceptCharset(Arrays.asList(StandardCharsets.UTF_8));
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, Collections.singletonMap("svg", encode(doc)));
        String svg = response.getBody();
        return svg.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public MediaType getMediaType() {
        return new MediaType("image", "svg+xml");
    }

    private String encode(File doc) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.lines(doc.toPath()).forEach(line -> {
            if (!line.isEmpty()) {
                sb.append(line).append(';');
            }
        });
        return sb.toString();
    }
}

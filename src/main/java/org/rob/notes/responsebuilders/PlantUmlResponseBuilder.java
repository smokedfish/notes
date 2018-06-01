package org.rob.notes.responsebuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

public class PlantUmlResponseBuilder implements ResponseBuilder {
    private final RestOperations restTemplate;

    public PlantUmlResponseBuilder(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public byte[] response(File doc) throws Exception {
        String url = "http://www.plantuml.com/plantuml/svg/{svg}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("image", "svg+xml"));
        headers.set(HttpHeaders.USER_AGENT, "spring resttemplate");
        headers.setAcceptCharset(Arrays.asList(StandardCharsets.UTF_8));
        Map<String, String> params = Collections.singletonMap("svg", PlantUmlEncoder.encode(Files.readAllBytes(doc.toPath())));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>("", headers), String.class, params);
        return response.getBody().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public MediaType getMediaType() {
        return new MediaType("image", "svg+xml");
    }

}

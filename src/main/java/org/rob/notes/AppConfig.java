package org.rob.notes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.rob.notes.responsebuilders.GravioResponseBuilder;
import org.rob.notes.responsebuilders.MarkDownResponseBuilder;
import org.rob.notes.responsebuilders.PlantUmlResponseBuilder;
import org.rob.notes.responsebuilders.ResponseBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    RestTemplate restTemplate() {
//        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
//        restTemplate.setInterceptors(Arrays.asList(new LoggingRequestInterceptor()));
//        return  restTemplate;
        return new RestTemplate();
    }

    @Bean
    Map<String, ResponseBuilder> responseBuilders(RestTemplate restTemplate) {
        Map<String, ResponseBuilder> responseBuilders = new HashMap<>();
        responseBuilders.put("md", new MarkDownResponseBuilder());
        responseBuilders.put("pu", new PlantUmlResponseBuilder(restTemplate));
        return responseBuilders;
    }
}
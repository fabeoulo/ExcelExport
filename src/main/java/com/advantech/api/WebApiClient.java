/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WebApiClient {

    private String baseUrl;
    private WebClient webClient;

    public WebApiClient() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    @Autowired
    public void setWebClient() {
        //set timeout 6s
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(6));

        //set cache 10MB and init
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> {
                            config.defaultCodecs()
                                    .maxInMemorySize(10 * 1024 * 1024);
                        })
                        .build())
                .build();

        //no need to creat after build
        //webClient = WebClient.create(); // use default setting
    }

    public WebApiUser getUserInAtmc(String jobnumber) {
        Mono<Object[]> body = webClient
                .get()
                .uri(baseUrl + jobnumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object[].class);
        try {
            Object[] bodyObject = body.block();
            List<WebApiUser> users = objectToUser(bodyObject);
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) { //WebClientException
            System.out.println("Exception e: " + e);
            return null;
        }
    }

    private List<WebApiUser> objectToUser(Object[] bodyObject) {
        if (bodyObject == null) {
            return new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        List<WebApiUser> l = mapper.convertValue(bodyObject, new TypeReference<List<WebApiUser>>() {
        });

        return l;
    }
}

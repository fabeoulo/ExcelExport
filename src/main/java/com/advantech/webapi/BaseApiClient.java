/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webapi;

import java.time.Duration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Justin.Yeh
 */
@Component
public abstract class BaseApiClient {

    protected final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    
    private String baseUrl;

    protected String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    //set cache mb
    protected WebClient getBufferClient(int mb) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(mb * 1024 * 1024)) // throw DataBufferLimitException
                .baseUrl(baseUrl)
                .build();
    }

    protected WebClient getClient() {
        return WebClient.create(baseUrl);
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webapi;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 *
 * @author Justin.Yeh
 */
@Component
public abstract class BaseApiClient {

    private String baseUrl;

    @Value("${WEBAPICLIENT.CONNECTION.TIMEOUT: 3000}")
    private Integer connection_Timeout;

    @Value("${WEBAPICLIENT.READ.TIMEOUT: 10}")
    private int write_Timeout;

    @Value("${WEBAPICLIENT.WRITE.TIMEOUT: 10}")
    private int read_Timeout;

    @Value("${WEBAPICLIENT.RESPONSE.TIMEOUT: 5}")
    private long response_Timeout;

    @Autowired
    private ReactorResourceFactory reactorResourceFactory;

    private final Function<HttpClient, HttpClient> mapper = client -> {
        return client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connection_Timeout)
                .doOnConnected(conn
                        -> conn
                        .addHandlerLast(new ReadTimeoutHandler(read_Timeout))
                        .addHandlerLast(new WriteTimeoutHandler(write_Timeout)))
                .responseTimeout(Duration.ofSeconds(response_Timeout));
    };

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected WebClient getClient() {
        return WebClient.create(baseUrl);
    }

    //set cache mb
    protected WebClient getBufferClient(int mb) {
        return customBuilder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(mb * 1024 * 1024)) //fix throw DataBufferLimitException
                .build();
    }

    private WebClient.Builder customBuilder() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(reactorResourceFactory, mapper));
    }
}

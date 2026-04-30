/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webscraper;

import com.advantech.webapi.BaseApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Justin.Yeh
 */
public class EzReactiveClient extends BaseApiClient {

    private static final Logger log = LoggerFactory.getLogger(EzReactiveClient.class);

    private String cookieHeader = "";

    private String userName;

    private String password;

    private final ObjectMapper mapper = new ObjectMapper();

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void loginAndSetCookie() {
        WebClient webClient = super.getClient();

        ClientResponse getResp = webClient.get()
                .uri("/net/Account/Login")
                .exchange()
                .block();

        List<String> cookies = getResp.headers().header(HttpHeaders.SET_COOKIE);
        cookieHeader = RetrieveCookieValue(cookies);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("UserName", userName);
        form.add("Password", password);
//        form.add("returnLinkID", "");
//        form.add("toURL", "");
//        form.add("ReturnUrl", "");

        ClientResponse postResp = webClient.post()
                .uri("/net/Account/Login")
                .header(HttpHeaders.COOKIE, cookieHeader)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .exchange()
                .block();

        // update cookie after login
        List<String> newCookies = postResp.headers().header(HttpHeaders.SET_COOKIE);
        if (!newCookies.isEmpty()) {
            String loginCookie = RetrieveCookieValue(newCookies);
            cookieHeader = cookieHeader + "; " + loginCookie;
        }
    }

    private String RetrieveCookieValue(List<String> cookies) {
        String kvCookie = cookies.stream()
                .map(c -> c.split(";")[0]) // Only get key=value
                .collect(Collectors.joining("; "));

//        log.info("New Cookie: " + kvCookie);
        return kvCookie;
    }

    public Object[] getCalendarInfo(int year, int month) {
        WebClient webClient = super.getClient();

        String uri = "/net/Home/GetCalendarInfo?year=" + year + "&month=" + month;

        Mono<Object[]> mono = webClient.get()
                .uri(uri)
                .header(HttpHeaders.COOKIE, cookieHeader)
                .retrieve()
                .bodyToMono(Object[].class);

        Object[] bodyObject = new Object[0];
        try {
            bodyObject = mono.block();
        } catch (Exception e) { //WebClientException
            log.error(e.getLocalizedMessage());
        }

        return bodyObject;
    }

    public <T> T convertObject(Object[] bodyObject, Supplier<T> supplier) {
        if (bodyObject == null) {
            return supplier.get();
        }

        T l = mapper.convertValue(bodyObject, new TypeReference<T>() {
        });

        return l;
    }

}

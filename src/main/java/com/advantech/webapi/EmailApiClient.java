/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webapi;

import com.advantech.webapi.model.EmailModel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

/**
 *
 * @author Justin.Yeh
 */
public class EmailApiClient extends BaseApiClient {

    private static final Logger log = LoggerFactory.getLogger(EmailApiClient.class);

    public Boolean sendEmail(EmailModel email) {
        Mono<Object> body = super.getBufferClient(5)
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(email)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve() // throw WebClientResponseException
                .bodyToMono(Object.class);
        try {
            Object bodyObject = body.block();
            log.info("SendEmail result: " + bodyObject.toString());
            return (Boolean) bodyObject;
        } catch (Exception e) { //WebClientException
            log.error(e.getLocalizedMessage());
            return false;
        }
    }
}

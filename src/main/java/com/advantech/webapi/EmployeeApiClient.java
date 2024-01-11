/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webapi;

import com.advantech.webapi.model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class EmployeeApiClient extends BaseApiClient {

    private static final Logger log = LoggerFactory.getLogger(EmployeeApiClient.class);

    public String getBaseUrl() {
        return super.getBaseUrl();
    }

    public Employee getUserInAtmc(String jobnumber) {
        Mono<Object[]> body = super.getClient()
                .get()
                .uri(jobnumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve() // throw WebClientResponseException
                .bodyToMono(Object[].class);
        try {
            Object[] bodyObject = body.block(REQUEST_TIMEOUT);
            List<Employee> users = objectToUser(bodyObject);
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) { //WebClientException
            System.out.println("Exception e: " + e);
            return null;
        }
    }

    private List<Employee> objectToUser(Object[] bodyObject) {
        if (bodyObject == null) {
            return new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Employee> l = mapper.convertValue(bodyObject, new TypeReference<List<Employee>>() {
        });

        return l;
    }
}

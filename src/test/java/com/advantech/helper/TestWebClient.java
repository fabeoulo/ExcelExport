/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import com.advantech.webapi.model.Employee;
import com.advantech.webapi.EmployeeApiClient;
import com.advantech.model.db1.User;
import com.advantech.service.db1.UserService;
import com.advantech.webapi.EmailApiClient;
import com.advantech.webapi.model.EmailModel;
import com.advantech.webscraper.EzReactiveClient;
import com.advantech.webscraper.EzScraperClient;
import com.advantech.webscraper.model.EzCalendar;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:servlet-context_test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestWebClient {

    @Autowired
    private EmployeeApiClient wc;

    @Autowired
    private EmailApiClient emailApiClient;

    @Autowired
    private EzReactiveClient ezReactiveClient;

    @Autowired
    private EzScraperClient ezScraperClient;

//    @Test
    public void testEzScraperClient() {
        DateTime now = DateTime.parse("2025-12-31");
        List<EzCalendar> la = new ArrayList<>();
        try {
            ezScraperClient.loginAndSetClient();
            for (int i = 1; i < 13; i++) {
                now = now.plusMonths(1);
                int y = now.year().get();
                int m = now.monthOfYear().get();
                HibernateObjectPrinter.print(y, m);

                List<EzCalendar> temp = ezScraperClient.getCalendarInfo(y, m);
                la.addAll(temp);
            }
        } catch (Exception e) {
            HibernateObjectPrinter.print(e.toString());
        }

    }

//    @Test
    public void testEzReactiveClient() {
        ezReactiveClient.loginAndSetCookie();
        Object[] bodyObject = ezReactiveClient.getCalendarInfo(2026, 1);

        List<EzCalendar> result = ezReactiveClient.convertObject(bodyObject, ArrayList::new);
    }

//    @Test
    public void testEmailApiClient() {
        EmailModel email = new EmailModel();
        String[] add = {"justin.yeh@advantech.com.tw"};
        email.setToAddresses(add);
        email.setSubject("subj");
        email.setBody("body");

        Boolean bodyObject = emailApiClient.sendEmail(email);
    }

//    @Test
    public void testGetUserInAtmc2() {
        String jobNo = "A-F0287";
        System.out.println("wc.baseUrl= " + wc.getBaseUrl());
        System.out.println(" wc.isUserInAtmc= " + wc.getUserInAtmc(jobNo));

        Employee atmcUser = GetUserInAtmc(jobNo);
        if (atmcUser != null) {
            System.out.println(" atmcUser.getEmplr_Id()= " + atmcUser.getEmplr_Id());
            HibernateObjectPrinter.print(" atmcUser.getLocal_Name()= " + atmcUser.getLocal_Name());
            System.out.println(" atmcUser.getEmail_Addr= " + atmcUser.getEmail_Addr());
        }
    }

    @Autowired
    private UserService userService;

//    @Test
//    @Transactional
//    @Rollback(false)
    public void testUserCheck() {
        List<User> userL = userService.findAll();
        for (User u : userL) {
            if (u.getJobnumber().equals(u.getUsername())) {
                Employee atmcUser = GetUserInAtmc(u.getJobnumber());
                if (atmcUser != null && atmcUser.getActive() == 1) {
                    u.setUsername(atmcUser.getLocal_Name());
                    u.setEmail(atmcUser.getEmail_Addr());
                    userService.save(u);
                }
            }
        }
    }

    private Employee GetUserInAtmc(String jobNo) {
        return wc.getUserInAtmc(jobNo);
    }

//    @Test
    public void testGetUserInAtmc() {

//        String jobNo = "A-10376";//syspo A-10376
//        String baseUrl = "http://172.22.250.120:7878/v1/Employee/";
//        WebClient webClient = WebClient.create();
//
//        System.out.println("baseUrl: " + baseUrl);
//        Mono<Object[]> body = webClient
//                .get()
//                .uri(baseUrl + jobNo)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(Object[].class);
//        Object[] atmcEMP;
//        List<Employee> urlist = new ArrayList<>();
//        try {
//            atmcEMP = body.block();
//            ObjectMapper mapper = new ObjectMapper();
//            urlist = mapper.convertValue(atmcEMP, new TypeReference<List<Employee>>() {
//            });
//        } catch (Exception e) {
//            System.out.println("Object[]  e: " + e);
////            return false;
//        }
//        if (urlist != null) {
//            for (Employee atmcUser : urlist) {
//                System.out.println(" atmcUser.getEmplr_Id()= " + atmcUser.Emplr_Id);
//                System.out.println(" atmcUser.getLocal_Name()= " + atmcUser.Local_Name);
//                System.out.println(" atmcUser.dep2= " + atmcUser.Dep2);
//            }
////            return true;
//        }
//        System.out.println("body: " + body);
//        Mono<AtmcEmp[]> bodyAtmcEmp2 = webClient
//                .get()
//                .uri(baseUrl + jobNo)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(AtmcEmp[].class);
//        AtmcEmp[] AE2;
//        try {
//            AE2 = bodyAtmcEmp2.block();
//        } catch (Exception e) {
//            System.out.println("AE2.Exception  e: " + e);
//            AE2 = new AtmcEmp[0];
//        }
//
//        List<AtmcEmp> urlist2 = Arrays.asList(AE2);
//        for (AtmcEmp atmcUser : urlist2) {
//            System.out.println(" AtmcEmp.Active()= " + atmcUser.Active);
//            System.out.println(" AtmcEmp.Shift_Id()= " + atmcUser.Shift_Id);
//            System.out.println(" AtmcEmp.Emplr_Id= " + atmcUser.Emplr_Id);
//        }
//        return urlist2;
    }

}

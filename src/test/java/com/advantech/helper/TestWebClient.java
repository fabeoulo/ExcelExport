/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import com.advantech.api.WebApiUser;
import com.advantech.api.WebApiClient;
import com.advantech.repo.db1.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Justin.Yeh
 */
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:servlet-context_test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestWebClient {

    @Autowired
    private UserRepository userRepo;

//   @Test
//    @Transactional //repo inside Transactional //Rollback default is true
//    @Rollback(false)
    public void testQuickInsert() {
        String jobnumber = "A-7060";
        userRepo.saveUserByProc(jobnumber);
        System.out.println("testQuickInsert ");
    }

    @Autowired
    @Qualifier("webApiClient")
    private WebApiClient wc;

    @Test
    public void GetUserInAtmc2() {
        String jobNo = "A-10376";//A-10376 sysop
        System.out.println("wc.baseUrl= " + wc.getBaseUrl());
        System.out.println(" wc.isUserInAtmc= " + wc.getUserInAtmc(jobNo));
        WebApiUser item = wc.getUserInAtmc(jobNo);
        if (item != null) {
            System.out.println(" item.getEmplr_Id()= " + item.Emplr_Id);
            System.out.println(" item.getLocal_Name()= " + item.Local_Name);
            System.out.println(" item.dep2= " + item.Dep2);
        }
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
//        List<WebApiUser> urlist = new ArrayList<>();
//        try {
//            atmcEMP = body.block();
//            ObjectMapper mapper = new ObjectMapper();
//            urlist = mapper.convertValue(atmcEMP, new TypeReference<List<WebApiUser>>() {
//            });
//        } catch (Exception e) {
//            System.out.println("Object[]  e: " + e);
////            return false;
//        }
//        if (urlist != null) {
//            for (WebApiUser item : urlist) {
//                System.out.println(" item.getEmplr_Id()= " + item.Emplr_Id);
//                System.out.println(" item.getLocal_Name()= " + item.Local_Name);
//                System.out.println(" item.dep2= " + item.Dep2);
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
//        for (AtmcEmp item : urlist2) {
//            System.out.println(" AtmcEmp.Active()= " + item.Active);
//            System.out.println(" AtmcEmp.Shift_Id()= " + item.Shift_Id);
//            System.out.println(" AtmcEmp.Emplr_Id= " + item.Emplr_Id);
//        }
//        return urlist2;
    }

}

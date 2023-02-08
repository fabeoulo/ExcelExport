/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import com.advantech.api.WebApiUser;
import com.advantech.api.WebApiClient;
import com.advantech.repo.db1.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    @Qualifier("webapiClient")
    private WebApiClient wc;

    @Test
    public void GetUserInAtmc2() {
        String jobNo = "A-10376";//A-10376 sysop
        System.out.println("wc.baseUrl= " + wc.getBaseUrl());
        System.out.println(" wc.getUserInAtmc= " + wc.getUserInAtmc(jobNo));
        WebApiUser item = wc.getUserInAtmc(jobNo);
        if (item != null) {
            System.out.println(" item.getEmplr_Id()= " + item.Emplr_Id);
            System.out.println(" item.getLocal_Name()= " + item.Local_Name);
            System.out.println(" item.dep2= " + item.Dep2);
        }
    }

//    @Test
    public void testGetUserInAtmc() {

        String jobNo = "A-10376";//syspo A-10376
        String baseUrl = "http://172.22.250.120:7878/v1/Employee/";
        WebClient webClient = WebClient.create();

        System.out.println("baseUrl: " + baseUrl);
        Mono<Object[]> body = webClient
                .get()
                .uri(baseUrl + jobNo)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object[].class);
        Object[] atmcEMP;
        List<WebApiUser> urlist = new ArrayList<>();
        try {
            atmcEMP = body.block();
            ObjectMapper mapper = new ObjectMapper();
            urlist = mapper.convertValue(atmcEMP, new TypeReference<List<WebApiUser>>() {
            });
        } catch (Exception e) {
            System.out.println("Object[]  e: " + e);
//            return false;
        }
        if (urlist != null) {
            for (WebApiUser item : urlist) {
                System.out.println(" item.getEmplr_Id()= " + item.Emplr_Id);
                System.out.println(" item.getLocal_Name()= " + item.Local_Name);
                System.out.println(" item.dep2= " + item.Dep2);
            }
//            return true;
        }
        System.out.println("body: " + body);

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

//    @XmlAccessorType(XmlAccessType.FIELD)
//    @XmlRootElement(name = "EmpInfo")
class AtmcEmp {//implements Serializable {

//        @XmlElement(name = "Emplr_Id")
    public String Emplr_Id;

//        @XmlElement(name = "Local_Name")
    public String Local_Name;

//        @XmlElement(name = "email_Addr")
    public String Email_Addr;

//        @XmlElement(name = "per_Level")
    public String Per_Level;

//        @XmlElement(name = "dep1")
    public String Dep1;

//        @XmlElement(name = "Dep2")
    public String Dep2;

//        @XmlElement(name = "Dep3")
    public String Dep3;

//        @XmlElement(name = "Cost_Center")
    public String Cost_Center;

//        @XmlElement(name = "MgrEmail_Addr")
    public String MgrEmail_Addr;

//        @XmlElement(name = "Active")
    public int Active;

//        @XmlElement(name = "Shift_Id")
    public String Shift_Id;

//        @XmlElement(name = "Dimission_Date")
    public String Dimission_Date;

    public String getEmplr_Id() {
        return Emplr_Id;
    }

    public void setEmplr_Id(String Emplr_Id) {
        this.Emplr_Id = Emplr_Id;
    }

//    public String getLocal_Name() {
//        return Local_Name;
//    }
//
//    public void setLocal_Name(String Local_Name) {
//        this.Local_Name = Local_Name;
//    }
//
//    public String getemail_Addr() {
//        return email_Addr;
//    }
//
//    public void setemail_Addr(String email_Addr) {
//        this.email_Addr = email_Addr;
//    }
//
//    public String getper_Level() {
//        return per_Level;
//    }
//
//    public void setper_Level(String per_Level) {
//        this.per_Level = per_Level;
//    }
//
//    public String getdep1() {
//        return dep1;
//    }
//
//    public void setdep1(String dep1) {
//        this.dep1 = dep1;
//    }
//        public String getDep2() {
//            return Dep2;
//        }
//
//        public void setDep2(String Dep2) {
//            this.Dep2 = Dep2;
//        }
//
//        public String getDep3() {
//            return Dep3;
//        }
//
//        public void setDep3(String Dep3) {
//            this.Dep3 = Dep3;
//        }
//
//        public String getCost_Center() {
//            return Cost_Center;
//        }
//
//        public void setCost_Center(String Cost_Center) {
//            this.Cost_Center = Cost_Center;
//        }
//
//        public String getMgrEmail_Addr() {
//            return MgrEmail_Addr;
//        }
//
//        public void setMgrEmail_Addr(String MgrEmail_Addr) {
//            this.MgrEmail_Addr = MgrEmail_Addr;
//        }
//
//        public int getActive() {
//            return Active;
//        }
//
//        public void setActive(int Active) {
//            this.Active = Active;
//        }
//
//        public String getShift_Id() {
//            return Shift_Id;
//        }
//
//        public void setShift_Id(String Shift_Id) {
//            this.Shift_Id = Shift_Id;
//        }
//
//        public String getDimission_Date() {
//            return Dimission_Date;
//        }
//
//        public void setDimission_Date(String Dimission_Date) {
//            this.Dimission_Date = Dimission_Date;
//        }
}

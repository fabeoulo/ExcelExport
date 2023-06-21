/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.job.SendLackWithStock;
import com.advantech.job.SendOvertimeReport;
import com.advantech.job.SendReport;
import com.advantech.job.SendWhReports;
import com.advantech.job.SyncData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestQuartzJob {

    @Autowired
    private SendReport reportJob;
    
    @Autowired
    private SyncData syncJob;
    
//    @Autowired
//    private SendWhReports whReportJob;
    
    @Autowired
    private SendOvertimeReport sendOvertimeReport;
    
    @Autowired
    private SendLackWithStock sendLackWithStock;
    
    @Value("${floor.five.fileLocation}")
    private String fileLocation;

//    @Test
    public void testMail() {
        reportJob.execute();
    }
    
//    @Test
    public void testSync() {
        syncJob.execute();
    }
    
//    @Test
    public void testSendWhReports() {
//        whReportJob.execute();
    }
    
//    @Test
    public void testSendSendOvertimeReport() {
        sendOvertimeReport.execute();
    }
   
    @Test
    public void testSendLackWithStock() {
        sendLackWithStock.execute();
    }
}

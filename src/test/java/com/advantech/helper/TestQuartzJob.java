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
import com.advantech.job.SendWhReportsDonghu;
import com.advantech.job.SendWhReportsLinkou;
import com.advantech.job.SyncData;
import com.advantech.job.SyncLackMrp;
import com.advantech.job.WareHourseAgent;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    
    @Autowired
    @Qualifier("sendWhReports")
    private SendWhReports whReportJob;
    
    @Autowired
    private SendWhReportsDonghu sendDonghu;
    
    @Autowired
    private SendWhReportsLinkou sendLinkou;
    
    @Autowired
    private SendOvertimeReport sendOvertimeReport;
	
    @Autowired
    private SendLackWithStock sendLackWithStock;
//
//    @Autowired
//    private SendRequiredToPMC sendRequiredToPMC;

    @Autowired
    private SyncLackMrp syncLackMrp;

    @Autowired
    private WareHourseAgent wareHourseAgent;
    
    @Value("${floor.five.fileLocation}")
    private String fileLocation;

//    @Test
    public void testWareHourseAgent() {
        wareHourseAgent.execute();
    }
    
//    @Test
    public void testMail() {
        reportJob.execute();
    }
    
    @Test
    public void testSync() {
        syncJob.execute();
    }

//    @Test
    public void testSendWhReportsDonghu() throws Exception {
//        sendDonghu.execute();
        DateTime dt = new DateTime(2024, 9,2, 0, 0, 0);
        String sb = sendDonghu.generateMailBody(dt);
    }
    
//    @Test
    public void testSendWhReportsLinkou() throws Exception {
//        sendLinkou.execute();
        DateTime dt = new DateTime(2024, 9,2, 0, 0, 0);
        String sb = sendLinkou.generateMailBody(dt);
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

////    @Test
//    public void testSendRequiredToPMC() {
//        sendRequiredToPMC.execute();
//    }

    @Test
    public void testsyncLackMrp() throws Exception {
        syncLackMrp.execute();
    }
}

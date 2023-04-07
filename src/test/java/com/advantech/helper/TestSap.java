/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.sap.SAPConn;
import com.advantech.sap.SapQueryPort;
import com.advantech.webservice.Factory;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Streams;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import java.net.URISyntaxException;
import java.util.Iterator;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author MFG.ESOP
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSap {

    @Autowired
    private SapQueryPort port;

    @Test
    public void testWarehouse() throws JCoException, URISyntaxException {

        JCoFunction function = port.getMaterialInfo("THM006325ZA", null);

        JCoTable master = function.getTableParameterList().getTable("ZWOMASTER");//调用接口返回结果
        JCoTable detail = function.getTableParameterList().getTable("ZWODETAIL");//调用接口返回结果

        for (int i = 0; i < master.getNumRows(); i++) {
            master.setRow(i);
            System.out.println(master.getString("MATNR"));
            System.out.println(master.getString("GSMNG"));
        }

        for (int i = 0; i < detail.getNumRows(); i++) {
            detail.setRow(i);
            System.out.println(detail.getString("AUFNR") + '\t' + CharMatcher.is('0').trimLeadingFrom(detail.getString("MATNR")));
            break;
        }

    }
    
//    @Test
    public void testUnitPrice() throws JCoException, URISyntaxException{
        JCoFunction function = port.getMaterialPrice("1700027469-01", Factory.TWM3);

        JCoTable master = function.getTableParameterList().getTable("LE_ZSD_COST");

        for (int i = 0; i < master.getNumRows(); i++) {
            master.setRow(i);
            System.out.println(master.getString("PE_STPRS"));
        }

    }

}

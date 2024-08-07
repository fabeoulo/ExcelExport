/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.Requisition;
import com.advantech.sap.SapQueryPort;
import com.advantech.sap.SapService;
import com.advantech.sap.SapMrpTbl;
import com.advantech.service.db1.RequisitionService;
import com.advantech.webservice.Factory;
import com.google.common.base.CharMatcher;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
    private RequisitionService rservice;

    @Autowired
    private SapQueryPort port;

    @Autowired
    private SapService sapService;

//    @Test
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
    public void testUnitPrice() throws JCoException, URISyntaxException {
        JCoFunction function = port.getMaterialPrice("1700027469-01", Factory.TWM3);

        JCoTable master = function.getTableParameterList().getTable("LE_ZSD_COST");

        for (int i = 0; i < master.getNumRows(); i++) {
            master.setRow(i);
            System.out.println(master.getString("PE_STPRS"));
        }

    }

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testStock() throws JCoException, URISyntaxException {
        List<Integer> listInt = Arrays.asList(85010);
        List<Requisition> rl = rservice.findAllByIdWithUserAndState(listInt);
        
//        port.setLgortGood();
        JCoFunction function = port.getMaterialStock(rl);
        JCoTable output = function.getTableParameterList().getTable("ZMARD_OUTPUT");

        Map<String, BigDecimal> stockMap = new HashMap<>();
        for (int i = 0; i < output.getNumRows(); i++) {
            output.setRow(i);
            String mat = removeLeadingZeros(output.getString("MATNR"));
            stockMap.merge(mat, new BigDecimal(output.getString("LABST")), BigDecimal::add);
        }
        HibernateObjectPrinter.print(stockMap);

        BigDecimal stock = stockMap.get(rl.get(0).getMaterialNumber());
        Boolean boo = !(stock == null || stock.compareTo(BigDecimal.ZERO) == 0);
        HibernateObjectPrinter.print(stock.compareTo(BigDecimal.ZERO));
    }

    private String removeLeadingZeros(String str) {
        return str.replaceAll("^0+", "");
    }

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testMrpCodeService() throws Exception {
        List<Integer> listInt = Arrays.asList(66125, 46232);
        List<Requisition> rl = rservice.findAllByIdWithUserAndState(listInt);
        Map<String, String> MrpMap = sapService.getMrpCodeMap(rl);
        HibernateObjectPrinter.print(MrpMap);
    }

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testMrpCode() throws JCoException, URISyntaxException {
        List<Integer> listInt = Arrays.asList(66125, 46232);
        List<Requisition> rl = rservice.findAllByIdWithUserAndState(listInt);
        List<SapMrpTbl> input = rl.stream()
                .map(l -> new SapMrpTbl(l.getMaterialNumber(), l.getWerk()))
                .collect(Collectors.toList());
        JCoFunction function = port.getMrpCode(input);
        JCoTable output = function.getTableParameterList().getTable("TBLOUT");

        Map<String, String> MrpMap = new HashMap<>();
        for (int i = 0; i < output.getNumRows(); i++) {
            output.setRow(i);
            String mat = removeLeadingZeros(output.getString("MATNR"));
            String key = mat + output.getString("WERKS");
            MrpMap.merge(key, output.getString("DISPO"), (oldValue, newValue) -> {
                return oldValue;
            });
        }
        HibernateObjectPrinter.print(MrpMap);
    }
}

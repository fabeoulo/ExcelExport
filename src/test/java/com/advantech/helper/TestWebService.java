/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.webservice.Factory;
import com.advantech.webservice.port.FqcKanBanQueryPort;
import com.advantech.webservice.port.QryWipAttQueryPort;
import com.advantech.webservice.root.WareHourseQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.tempuri.InsertWareHourseEFlowMas;
import org.tempuri.InsertWareHourseEFlowMasResponse;
import org.tempuri.ObjectFactory;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestWebService {

    @Autowired
    private FqcKanBanQueryPort kanbanPort;

    @Autowired
    private QryWipAttQueryPort modelNameQryPort;

//    @Test
    public void test1() throws Exception {

        List l = kanbanPort.query(Factory.TWM3);

        HibernateObjectPrinter.print(l);
    }

    @Test
    public void test2() throws Exception {

        String po = "THK002854Z22A";

        List l = modelNameQryPort.query(po, Factory.TWM3);

        HibernateObjectPrinter.print(l);
    }

    @Autowired
    @Qualifier("resourceMap")
    private Map<Factory, WebServiceTemplate> resourceMap;

    @Test
    public void wareHourseQuery() throws Exception {

        ObjectFactory factory = new ObjectFactory();
        InsertWareHourseEFlowMas wh = factory.createInsertWareHourseEFlowMas();
        WareHourseQuery whq = new WareHourseQuery();
        WareHourseQuery.RequitionDetail aD = new WareHourseQuery.RequitionDetail();
        aD.setPo("THL010291ZA");
        aD.setMaterialNo("1930004607");
        aD.setRequireQty(1);
        aD.setReason("THL010291ZA 超領急件");
        aD.setJobnumber("A-8754");
        aD.setUserName("5F 鄭麓成");

        whq.setRequitions(Lists.newArrayList(aD, aD));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(whq);

        HibernateObjectPrinter.print(jsonString);
        wh.setSparam(jsonString);

        Factory f = Factory.M3WH;
        WebServiceTemplate t = resourceMap.get(f);
        checkState(t != null, f.token() + " webService template is not inject");
        InsertWareHourseEFlowMasResponse response = (InsertWareHourseEFlowMasResponse) t.marshalSendAndReceive(wh);
        String s = response.getInsertWareHourseEFlowMasResult();
        checkState(s.equals(""),"request fail.");
//        HibernateObjectPrinter.print(response);

    }

}

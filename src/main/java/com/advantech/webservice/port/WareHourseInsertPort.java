/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice.port;

import com.advantech.model.db1.Requisition;
import com.advantech.webservice.Factory;
import com.advantech.webservice.root.WareHourseInsert;
import com.advantech.webservice.root.WareHourseInsert.RequitionDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.tempuri.InsertWareHourseEFlowMas;
import org.tempuri.InsertWareHourseEFlowMasResponse;
import org.tempuri.ObjectFactory;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WareHourseInsertPort {

    private static final Logger logger = LoggerFactory.getLogger(WareHourseInsertPort.class);

    @Autowired
    @Qualifier("resourceMap")
    private Map<Factory, WebServiceTemplate> resourceMap;

    public String insertWareHourse(List<Requisition> requisitions, String commitJobNo) throws Exception {
        
        String response = "";
        Map<String, List<Requisition>> groupedMap = requisitions.stream()
                .collect(Collectors.groupingBy(Requisition::getWerk));
        for (Map.Entry<String, List<Requisition>> entry : groupedMap.entrySet()) {
            String werk = entry.getKey();
            List<Requisition> rl = entry.getValue();

            List<RequitionDetail> details = new ArrayList<>();
            for (Requisition r : rl) {
                String userInfo = r.getUser().getUsername() + " " + r.getFloor().getName();
                String reason = r.getRemark() + " " + userInfo;

                RequitionDetail aD = new RequitionDetail();
                aD.setPo(r.getPo());
                aD.setMaterialNo(r.getMaterialNumber());
                aD.setRequireQty(Math.abs(r.getAmount()));
                aD.setJobnumber(commitJobNo);
                aD.setUserName(userInfo);
                aD.setReason(reason);
                details.add(aD);
            }
            WareHourseInsert whi = new WareHourseInsert();
            whi.setRequitions(details);
            whi.setPlant(werk);

            String jsonString = getJsonString(whi);
            response += eFlowSendAndReceive(jsonString, Factory.M9WH);
        }
        return response;
    }

    private String getJsonString(Object o) throws Exception {
        return new ObjectMapper().writeValueAsString(o);
    }

    private String eFlowSendAndReceive(String jsonString, Factory f) {
        InsertWareHourseEFlowMas wh = new ObjectFactory().createInsertWareHourseEFlowMas();
        wh.setSparam(jsonString);
        WebServiceTemplate t = resourceMap.get(f);
        checkState(t != null, f.token() + " webService template is not inject");
        InsertWareHourseEFlowMasResponse response = (InsertWareHourseEFlowMasResponse) t.marshalSendAndReceive(wh);
        return response.getInsertWareHourseEFlowMasResult();
//        return "";
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice;

import com.advantech.model.db1.Requisition;
import com.advantech.sap.SapService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.trigger.RequisitionStateChangeTrigger;
import com.advantech.webservice.port.WareHourseInsertPort;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WareHourseService {

    private static final Logger log = LoggerFactory.getLogger(WareHourseService.class);

    @Autowired
    private SapService sapService;

    private final String schema = "[庫存量：";
    private final String tail = "],";
    private final String regex = "\\" + schema + "(\\d+(\\.\\d+)?)" + tail;

    @Autowired
    private WareHourseInsertPort whInsertPort;

    @Autowired
    private RequisitionService service;

    @Autowired
    private RequisitionStateChangeTrigger trigger;

    public String insertEflow(List<Requisition> l, String commitJobNo) {
        String result;

        //get sap stock
        Map<String, BigDecimal> stockMapInit = new HashMap<>();
        try {
            stockMapInit = sapService.getStockMap(l);
        } catch (Exception ex) {
            log.error("Get StockMap fail : " + ex);
        }

        // filter Requisition by checking stock
        Map<String, BigDecimal> stockMap = stockMapInit;
        String[] stockMsg = {""};
        List<Requisition> passList = new ArrayList<>();
        List<Requisition> lackList = new ArrayList<>();
        List<Requisition> noStockList = new ArrayList<>();

        l.forEach(t -> {
            String mat = t.getMaterialNumber();
            BigDecimal stock = stockMap.get(mat);
            if (stock != null && stock.compareTo(BigDecimal.ZERO) == 0) {
                noStockList.add(t);
            } else if (stock != null && stock.compareTo(BigDecimal.valueOf(Math.abs(t.getAmount()))) == -1) {
                lackList.add(t);
            } else {
                passList.add(t);
                return;
            }

            String newStock = schema + stock + tail;
            t.setRemark(replaceStockRemark(t.getRemark(), newStock));
            stockMsg[0] += " 料號：" + mat + " " + newStock;
        });

        //insert WH
        try {
            String response = whInsertPort.insertWareHourse(passList, commitJobNo);
            if ("".equals(response)) {

                service.updateWithStateAndEvent(lackList, 4);
                service.updateWithStateAndEvent(passList, 5);
                service.updateWithStateAndEvent(noStockList, 2);
                trigger.checkRepair(l);

                if (stockMsg[0].isEmpty()) {
                    result = "success";
                } else {
                    result = "部份成功,有庫存不足." + stockMsg[0];
                }
            } else {
                result = "InsertEflow response:=" + response;
            }
        } catch (Exception ex) {
            result = "InsertEflow exception : " + ex;
            log.error(result);
        }
        return result;
    }

    private String replaceStockRemark(String preRemark, String newStock) {
        String remarkStock;

        if (preRemark.matches(".*" + regex + ".*")) {
            remarkStock = preRemark.replaceAll(regex, newStock);
        } else {
            remarkStock = preRemark + " " + newStock;
        }

        return remarkStock;
    }
}

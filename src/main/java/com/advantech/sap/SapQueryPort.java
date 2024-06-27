/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.sap;

import com.advantech.model.db1.Requisition;
import com.advantech.webservice.Factory;
import com.google.common.collect.Lists;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import java.net.URISyntaxException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class SapQueryPort {

    @Autowired
    private SAPConn1 sapConn;

    private final List<String> lgortGood = Lists.newArrayList("0008", "0012", "0015", "CUST", "0018", "0016");
    private final List<String> lgortOther = Lists.newArrayList("0055", "0058");
    private List<String> lgort;

    @PostConstruct
    private void setLgortAll() {
        setLgortGood();
        lgort.addAll(lgortOther);
    }

    public void setLgortGood() {
        lgort = Lists.newArrayList(lgortGood);
    }

    public JCoFunction getMaterialInfo(String po, Factory factory) throws JCoException, URISyntaxException {
        JCoFunction function;
        JCoDestination destination = sapConn.getConn();

        //调用ZCHENH001函数
        function = destination.getRepository().getFunction("ZGET_SAP_SODNWO_DATA_CK");

        JCoParameterList input = function.getImportParameterList();

        input.setValue("WONO", po);
        input.setValue("SDATE", "");
        input.setValue("EDATE", "");
        input.setValue("SPFLG", "");
        input.setValue("PLANT", factory == null ? "" : "TW" + factory.token());

        function.execute(destination);
        return function;

    }

    public JCoFunction getMaterialPrice(String material, Factory factory) throws JCoException, URISyntaxException {
        JCoFunction function;
        JCoDestination destination = sapConn.getConn();

        //调用ZCHENH001函数
        function = destination.getRepository().getFunction("Z_SD_SEARCH_COST_PRICE");

        JCoParameterList input = function.getImportParameterList();

        input.setValue("MATNR", material);
        input.setValue("WERKS", "TW" + factory.token());

        function.execute(destination);
        return function;

    }

    public JCoFunction getMaterialStock(List<Requisition> rl) throws JCoException, URISyntaxException {
        JCoFunction function;
        JCoDestination destination = sapConn.getConn();

        function = destination.getRepository().getFunction("ZCN_GET_BIN_STOCK_N");

        JCoTable zmardTable = function.getTableParameterList().getTable("ZMARD_INPUT");
        for (Requisition detail : rl) {
            String werk = "TWM3".equals(detail.getWerk()) ? "TWM9" : detail.getWerk();
            for (String l : lgort) {
                zmardTable.appendRow();
                zmardTable.setValue("WERKS", werk);
                zmardTable.setValue("MATNR", detail.getMaterialNumber());
                zmardTable.setValue("LGORT", l);
            }
        }

        function.execute(destination);
        return function;
    }

    public JCoFunction getMrpCode(List<SapMrpTbl> tblIns) throws JCoException, URISyntaxException {
        JCoFunction function;
        JCoDestination destination = sapConn.getConn();

        function = destination.getRepository().getFunction("ZPP_MATERIAL_MASTER_RFC");

        JCoTable zmardTable = function.getTableParameterList().getTable("TBLIN");
        for (SapMrpTbl detail : tblIns) {
            zmardTable.appendRow();
            zmardTable.setValue("WERKS", detail.getWerk());
            zmardTable.setValue("MATNR", detail.getMaterialNumber());
        }

        function.execute(destination);
        return function;
    }
}

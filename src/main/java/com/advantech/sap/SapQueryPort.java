/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author MFG.ESOP
 */
@Component
public class SapQueryPort {

    @Autowired
    private SAPConn1 sapConn;

    public JCoFunction getMaterialInfo(String po) throws JCoException, URISyntaxException {
        JCoFunction function;
        JCoDestination destination = sapConn.getConn();

        //调用ZCHENH001函数
        function = destination.getRepository().getFunction("ZGET_SAP_SODNWO_DATA_CK");

        JCoParameterList input = function.getImportParameterList();

        input.setValue("WONO", po);
        input.setValue("SDATE", "");
        input.setValue("EDATE", "");
        input.setValue("SPFLG", "");
        input.setValue("PLANT", "TWM3");

        function.execute(destination);

        return function;

    }
}

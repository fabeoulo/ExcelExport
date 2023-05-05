/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.sap;

import com.advantech.model.db1.Requisition;
import com.advantech.webservice.Factory;
import com.google.common.base.CharMatcher;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Wei.Cheng
 */
@Service
public class SapService {

    @Autowired
    private SapQueryPort port;

    public List<SapMaterialInfo> retrieveSapMaterialInfos(String po, String... materialNumbers) throws JCoException, URISyntaxException {

        List<SapMaterialInfo> result = new ArrayList();

        JCoFunction function = port.getMaterialInfo(po, null);
        JCoTable detailTable = function.getTableParameterList().getTable("ZWODETAIL");//调用接口返回结果
        JCoTable masterTable = function.getTableParameterList().getTable("ZWOMASTER");//调用接口返回结果

        if (detailTable.getNumRows() == 0) {
            return result;
        }

        String modelName = detailTable.getString("BAUGR").trim();

        //Retrieve model name info
        for (int i = 0; i < detailTable.getNumRows(); i++) {
            detailTable.setRow(i);

            String materialNumber = detailTable.getString("MATNR");
            materialNumber = CharMatcher.is('0').trimLeadingFrom(materialNumber);

            if (Arrays.stream(materialNumbers).anyMatch(materialNumber::equals)) {

                SapMaterialInfo pojo = new SapMaterialInfo();
                pojo.setPo(po);
                pojo.setModelName(modelName);
                pojo.setMaterialNumber(materialNumber);
                pojo.setAmount(new BigDecimal(detailTable.getString("BDMNG").trim()));
                pojo.setStorageSpaces(detailTable.getString("STORLOC_BIN").trim());

                pojo.setWerk(detailTable.getString("WERKS").trim());
                Factory f = Factory.valueOf(detailTable.getString("WERKS").trim());

                JCoFunction function2 = port.getMaterialPrice(materialNumber, f);
                BigDecimal unitPrice = this.retrievePriceFromTable(function2.getTableParameterList().getTable("LE_ZSD_COST"));
                pojo.setUnitPrice(unitPrice);
                pojo.setTotalQty(new BigDecimal(masterTable.getString("GSMNG")));
                result.add(pojo);
            }
        }

        return result;
    }

    private BigDecimal retrievePriceFromTable(JCoTable table) {
        for (int i = 0; i < table.getNumRows(); i++) {
            table.setRow(i);
            return new BigDecimal(table.getString("PE_STPRS"));
        }
        return BigDecimal.ZERO;
    }
    
    public Map<String, BigDecimal> getStockMap(List<Requisition> l) throws Exception {
        JCoFunction function = port.getMaterialStock(l);
        JCoTable output = function.getTableParameterList().getTable("ZMARD_OUTPUT");
        Map<String, BigDecimal> stockMap = new HashMap<>();
        for (int i = 0; i < output.getNumRows(); i++) {
            output.setRow(i);
            String mat = removeLeadingZeros(output.getString("MATNR"));
            stockMap.merge(mat, new BigDecimal(output.getString("LABST")), BigDecimal::add);
        }
        return stockMap;
    }

    private String removeLeadingZeros(String str) {
        return str.replaceAll("^0+", "");
    }
}

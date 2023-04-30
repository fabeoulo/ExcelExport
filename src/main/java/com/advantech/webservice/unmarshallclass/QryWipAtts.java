/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice.unmarshallclass;

import com.advantech.model.db2.QryWipAtt;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Wei.Cheng
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "NewDataSet")
public class QryWipAtts implements Serializable, QueryResult<QryWipAtt> {

    @XmlElement(name = "Table1", type = QryWipAtt.class)
    private List<QryWipAtt> QryData;

    @Override
    public List<QryWipAtt> getQryData() {
        return QryData;
    }

    @Override
    public void setQryData(List<QryWipAtt> QryData) {
        this.QryData = QryData;
    }
}

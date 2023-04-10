/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice.unmarshallclass;

import com.advantech.model.db1.FqcKanBan;
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
public class FqcKanBans implements Serializable, QueryResult<FqcKanBan> {

    @XmlElement(name = "Table1", type = FqcKanBan.class)
    private List<FqcKanBan> QryData;

    @Override
    public List<FqcKanBan> getQryData() {
        return QryData;
    }

    @Override
    public void setQryData(List<FqcKanBan> QryData) {
        this.QryData = QryData;
    }
}

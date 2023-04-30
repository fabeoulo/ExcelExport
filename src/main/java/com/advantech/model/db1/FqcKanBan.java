/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.advantech.webservice.XmlDateAdapter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "FqcKanBan")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Table1")
public class FqcKanBan implements Serializable {

    private int id;

    @XmlElement(name = "WIP_NO")
    private String po;

    @XmlElement(name = "ITEM_NO")
    private String modelName;

    @XmlElement(name = "INHOUSE_NO")
    private String inhouseNo;

    @XmlElement(name = "INHOUSE_QTY")
    private int inhouseQty;

    @XmlElement(name = "CREATE_DATE")
    @XmlJavaTypeAdapter(XmlDateAdapter.class)
    private Date createDate;

    @XmlElement(name = "UPDATE_DATE")
    @XmlJavaTypeAdapter(XmlDateAdapter.class)
    private Date updateDate;

    //檢測結果
    @XmlElement(name = "QA_RESULT")
    private String qaResult;

    @XmlElement(name = "MEMO")
    private String memo;

    @XmlElement(name = "TIME")
    private BigDecimal time;

    private User lastEditor;

    private int state = 0;

    private int priority = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "po", length = 30, nullable = false)
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    @Column(name = "modelName", length = 30, nullable = false)
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Column(name = "inhouseNo", length = 30, nullable = false)
    public String getInhouseNo() {
        return inhouseNo;
    }

    public void setInhouseNo(String inhouseNo) {
        this.inhouseNo = inhouseNo;
    }

    @Column(name = "inhouseQty", nullable = false)
    public int getInhouseQty() {
        return inhouseQty;
    }

    public void setInhouseQty(int inhouseQty) {
        this.inhouseQty = inhouseQty;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", nullable = false, updatable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date", updatable = false)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "qa_result", length = 1, nullable = false)
    public String getQaResult() {
        return qaResult;
    }

    public void setQaResult(String qaResult) {
        this.qaResult = qaResult;
    }

    @Column(name = "memo", length = 100)
    public String getMemo() {
        return "".equals(memo) ? null : memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name = "[time]", nullable = false, precision = 10, scale = 2)
    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastEditor_id")
    public User getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(User lastEditor) {
        this.lastEditor = lastEditor;
    }

    @Column(name = "[state]", nullable = false)
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Column(name = "[priority]", nullable = false)
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.po);
        hash = 79 * hash + Objects.hashCode(this.modelName);
        hash = 79 * hash + Objects.hashCode(this.inhouseNo);
        hash = 79 * hash + this.inhouseQty;
        hash = 79 * hash + Objects.hashCode(this.createDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FqcKanBan other = (FqcKanBan) obj;
        if (this.inhouseQty != other.inhouseQty) {
//            System.out.printf("this %d, other %d\r\n", this.inhouseQty, other.inhouseQty);
            return false;
        }
        if (!Objects.equals(this.po, other.po)) {
//            System.out.printf("this %s, other %s\r\n", this.po, other.po);
            return false;
        }
        if (!Objects.equals(this.modelName, other.modelName)) {
//            System.out.printf("this %s, other %s\r\n", this.modelName, other.modelName);
            return false;
        }
        if (!Objects.equals(this.inhouseNo, other.inhouseNo)) {
//            System.out.printf("this %s, other %s\r\n", this.inhouseNo, other.inhouseNo);
            return false;
        }
        if ((this.createDate == null && other.createDate != null)
                || (this.createDate != null && other.createDate == null)
                || !Objects.equals(this.createDate.getTime() / 100, other.createDate.getTime() / 100)) {
            //System.out.printf(" This createDate: %s, other: %s ", this.createDate.toString(), other.createDate.toString());
            System.out.printf("this %d, other %d\r\n", this.createDate.getTime() / 100, other.createDate.getTime() / 100);
            return false;
        }
        return true;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "Requisition")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Requisition implements Serializable {

    private int id;
    private String po;
    private String materialNumber;
    private int amount;
    private String modelName;
    private BigDecimal unitPrice;
    private String werk;
    private BigDecimal poQty;
    private BigDecimal materialQty;
    private String storageSpaces;
    private Floor floor;
    private RequisitionState requisitionState;
    private RequisitionFlow requisitionFlow;
    private RequisitionReason requisitionReason;
    private RequisitionType requisitionType;
    private RequisitionCateIms requisitionCateIms;
    private RequisitionCateMes requisitionCateMes;
    private String requisitionCateMesCustom;
    private String materialType;
    private String materialBoardSn;
    private User user;
    private Date createDate;
    private Date lastUpdateDate;
    private String remark;
    private Date receiveDate;
    private Date returnDate;
    private String isUrgent;
    private String returnOrderNo;

    @JsonIgnore
    private Set<RequisitionEvent> requisitionEvents = new HashSet(0);

    private int lackingFlag = 0;

    public Requisition() {
    }

    public Requisition(String po, String materialNumber, int amount, RequisitionReason requisitionReason, User user, String remark, Floor floor) {
        this.po = po;
        this.materialNumber = materialNumber;
        this.amount = amount;
        this.requisitionReason = requisitionReason;
        this.user = user;
        this.remark = remark;
        this.floor = floor;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "po", length = 50, nullable = false)
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    @Column(name = "materialNumber", length = 50, nullable = false)
    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    @Column(name = "modelName")
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Column(name = "unitPrice")
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Column(name = "werk", length = 10)
    public String getWerk() {
        return werk;
    }

    public void setWerk(String werk) {
        this.werk = werk;
    }

    @Column(name = "amount")
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionState_id")
    public RequisitionState getRequisitionState() {
        return requisitionState;
    }

    public void setRequisitionState(RequisitionState requisitionState) {
        this.requisitionState = requisitionState;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionReason_id")
    public RequisitionReason getRequisitionReason() {
        return requisitionReason;
    }

    public void setRequisitionReason(RequisitionReason requisitionReason) {
        this.requisitionReason = requisitionReason;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionType_id")
    public RequisitionType getRequisitionType() {
        return requisitionType;
    }

    public void setRequisitionType(RequisitionType requisitionType) {
        this.requisitionType = requisitionType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createDate", length = 23, updatable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdateDate", length = 23)
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "receiveDate", length = 23)
    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "returnDate", length = 23)
    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    @Column(name = "materialType")
    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionFlow_id")
    public RequisitionFlow getRequisitionFlow() {
        return requisitionFlow;
    }

    public void setRequisitionFlow(RequisitionFlow requisitionFlow) {
        this.requisitionFlow = requisitionFlow;
    }

    @Column(name = "remark", length = 150)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "requisition")
    public Set<RequisitionEvent> getRequisitionEvents() {
        return requisitionEvents;
    }

    public void setRequisitionEvents(Set<RequisitionEvent> requisitionEvents) {
        this.requisitionEvents = requisitionEvents;
    }

    @Column(name = "lackingFlag")
    public int getLackingFlag() {
        return lackingFlag;
    }

    public void setLackingFlag(int lackingFlag) {
        this.lackingFlag = lackingFlag;
    }

    public BigDecimal getPoQty() {
        return poQty;
    }

    public void setPoQty(BigDecimal poQty) {
        this.poQty = poQty;
    }

    public BigDecimal getMaterialQty() {
        return materialQty;
    }

    public void setMaterialQty(BigDecimal materialQty) {
        this.materialQty = materialQty;
    }

    @Column(name = "storageSpaces", length = 255)
    public String getStorageSpaces() {
        return storageSpaces;
    }

    public void setStorageSpaces(String storageSpaces) {
        this.storageSpaces = storageSpaces;
    }

    @Column(name = "materialBoardSn", length = 50)
    public String getMaterialBoardSn() {
        return materialBoardSn;
    }

    public void setMaterialBoardSn(String materialBoardSn) {
        this.materialBoardSn = materialBoardSn;
    }

    @Column(name = "isUrgent", length = 10)
    public String getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        this.isUrgent = isUrgent;
    }

    @Column(name = "returnOrderNo", length = 50)
    public String getReturnOrderNo() {
        return returnOrderNo;
    }

    public void setReturnOrderNo(String returnOrderNo) {
        this.returnOrderNo = returnOrderNo;
    }

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionCateIms_id")
    public RequisitionCateIms getRequisitionCateIms() {
        return requisitionCateIms;
    }

    public void setRequisitionCateIms(RequisitionCateIms requisitionCateIms) {
        this.requisitionCateIms = requisitionCateIms;
    }

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionCateMes_id")
    public RequisitionCateMes getRequisitionCateMes() {
        return requisitionCateMes;
    }

    public void setRequisitionCateMes(RequisitionCateMes requisitionCateMes) {
        this.requisitionCateMes = requisitionCateMes;
    }

    @Column(name = "requisitionCateMesCustom", length = 100)
    public String getRequisitionCateMesCustom() {
        return requisitionCateMesCustom;
    }

    public void setRequisitionCateMesCustom(String requisitionCateMesCustom) {
        this.requisitionCateMesCustom = requisitionCateMesCustom;
    }

}

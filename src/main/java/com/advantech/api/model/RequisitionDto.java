/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api.model;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionCateIms;
import com.advantech.model.db1.RequisitionCateMes;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

/**
 *
 * @author Justin.Yeh
 */
public class RequisitionDto {

    @JsonProperty("wo")
    private String po;
    private String modelName;
    private BigDecimal poQty;
    @JsonProperty("materialNo")
    private String materialNumber;
    @JsonProperty("requestQty")
    private int amount;
    private BigDecimal unitPrice;
    @JsonProperty("flowName")
    private String requisitionFlowName;
    @JsonProperty("reasonName")
    private String requisitionReasonName;
    @JsonProperty("imsCateName")
    private String requisitionCateImsName;
    @JsonProperty("mesCateName")
    private String requisitionCateMesName;
    @JsonProperty("noGoodReason")
    private String materialType;
    @JsonProperty("materialNoGoodSn")
    private String materialBoardSn;
    private String floorName;
    @JsonProperty("stateName")
    private String requisitionStateName;
    private Date returnDate;
    @JsonProperty("typeName")
    private String requisitionTypeName;
    private String remark;
    private String returnOrderNo;
    @JsonProperty("urgent")
    private String isUrgent;

    public RequisitionDto() {
    }

    public RequisitionDto(Requisition r) {
        this.po = r.getPo();
        this.modelName = r.getModelName();
        this.poQty = r.getPoQty();
        this.materialNumber = r.getMaterialNumber();
        this.amount = r.getAmount();
        this.unitPrice = r.getUnitPrice();
        this.requisitionFlowName = r.getRequisitionFlow().getName();
        this.requisitionReasonName = r.getRequisitionReason().getName();
        this.requisitionCateImsName = Optional.ofNullable(r.getRequisitionCateIms()).map(RequisitionCateIms::getName).orElse("");
        this.requisitionCateMesName = Optional.ofNullable(r.getRequisitionCateMes()).map(RequisitionCateMes::getName).orElse(r.getRequisitionCateMesCustom());
        this.materialType = r.getMaterialType();
        this.materialBoardSn = r.getMaterialBoardSn();
        this.floorName = r.getFloor().getName();
        this.requisitionStateName = r.getRequisitionState().getName();
        this.returnDate = r.getReturnDate();
        this.requisitionTypeName = r.getRequisitionType().getName();
        this.remark = r.getRemark();
        this.returnOrderNo = r.getReturnOrderNo();
        this.isUrgent = r.getIsUrgent();
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getRequisitionFlowName() {
        return requisitionFlowName;
    }

    public void setRequisitionFlowName(String requisitionFlowName) {
        this.requisitionFlowName = requisitionFlowName;
    }

    public BigDecimal getPoQty() {
        return poQty;
    }

    public void setPoQty(BigDecimal poQty) {
        this.poQty = poQty;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getRequisitionStateName() {
        return requisitionStateName;
    }

    public void setRequisitionStateName(String requisitionStateName) {
        this.requisitionStateName = requisitionStateName;
    }

    public String getRequisitionReasonName() {
        return requisitionReasonName;
    }

    public void setRequisitionReasonName(String requisitionReasonName) {
        this.requisitionReasonName = requisitionReasonName;
    }

    public String getRequisitionTypeName() {
        return requisitionTypeName;
    }

    public void setRequisitionTypeName(String requisitionTypeName) {
        this.requisitionTypeName = requisitionTypeName;
    }

    public String getRequisitionCateImsName() {
        return requisitionCateImsName;
    }

    public void setRequisitionCateImsName(String requisitionCateImsName) {
        this.requisitionCateImsName = requisitionCateImsName;
    }

    public String getRequisitionCateMesName() {
        return requisitionCateMesName;
    }

    public void setRequisitionCateMesName(String requisitionCateMesName) {
        this.requisitionCateMesName = requisitionCateMesName;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialBoardSn() {
        return materialBoardSn;
    }

    public void setMaterialBoardSn(String materialBoardSn) {
        this.materialBoardSn = materialBoardSn;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReturnOrderNo() {
        return returnOrderNo;
    }

    public void setReturnOrderNo(String returnOrderNo) {
        this.returnOrderNo = returnOrderNo;
    }

    public String getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        this.isUrgent = isUrgent;
    }

}

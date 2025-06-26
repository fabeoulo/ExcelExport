/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.sap;

import java.math.BigDecimal;

/**
 *
 * @author Wei.Cheng
 */
public class SapMaterialInfo {

    private String po;
    private String modelName;
    private String materialNumber;
    private BigDecimal amount;
    private BigDecimal unitPrice;
    private BigDecimal poQty;
    private String storageSpaces;
    private String werk;

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getPoQty() {
        return poQty;
    }

    public void setPoQty(BigDecimal poQty) {
        this.poQty = poQty;
    }

    public String getStorageSpaces() {
        return storageSpaces;
    }

    public void setStorageSpaces(String storageSpaces) {
        this.storageSpaces = storageSpaces;
    }

    public String getWerk() {
        return werk;
    }

    public void setWerk(String werk) {
        this.werk = werk;
    }

}

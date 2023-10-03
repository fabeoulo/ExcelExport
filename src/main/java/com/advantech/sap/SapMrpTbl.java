/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.sap;

/**
 *
 * @author Justin.Yeh
 */
public class SapMrpTbl {

    private String materialNumber;
    private String werk;
    private String mrpCode;

    public SapMrpTbl() {
    }

    public SapMrpTbl(String materialNumber, String werk) {
        this.materialNumber = materialNumber;
        this.werk = werk;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getWerk() {
        return werk;
    }

    public void setWerk(String werk) {
        this.werk = werk;
    }

    public String getMrpCode() {
        return mrpCode;
    }

    public void setMrpCode(String mrpCode) {
        this.mrpCode = mrpCode;
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.advantech.model.db3;

import java.math.BigDecimal;

/**
 *
 * @author Justin.Yeh
 */
public interface WhReport {

    public String getDateField();

    public Integer getQuantity();

    public BigDecimal getSapWorktime();

    public BigDecimal getSapOutputValue();

    public String getPlant();

    public String getWorkCenter();

    default BigDecimal getSapWorktimeScaled() {
        BigDecimal sapWorktime = getSapWorktime();
        return sapWorktime != null ? sapWorktime.setScale(3) : BigDecimal.ZERO.setScale(3);
    }

    default String convertPlant() {
        String pType;
        switch (getPlant()) {
            case "TWM3":
                pType = "MH11";
                break;
            case "TWM6":
                pType = "MH12";
                break;
            case "TWM9":
                pType = "MH10";
                break;
            case "TWM8":
                pType = "TWM8";
                break;
            default:
                pType = "";
        }
        return pType;
    }

    default int getQuantityNotNull() {
        return getQuantity() == null ? 0 : getQuantity();
    }

    default BigDecimal getSapOutputValueCutDigits() {
        BigDecimal sapOutputValue = getSapOutputValue();
        return sapOutputValue == null ? BigDecimal.ZERO
                : sapOutputValue.subtract(sapOutputValue.remainder(new BigDecimal(10)));
    }
}

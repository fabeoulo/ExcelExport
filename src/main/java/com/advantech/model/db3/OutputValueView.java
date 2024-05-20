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
public interface OutputValueView {

    public String getErdat();// DataDate

    public String getPlant();

    public String getZzcftno();// WorkCenter    

    public BigDecimal getQuantity();

    public BigDecimal getStandardCost();
}

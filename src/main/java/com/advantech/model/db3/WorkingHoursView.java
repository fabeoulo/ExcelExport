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
public interface WorkingHoursView {

    public String getBudat();// DataDate

    public String getPlant();

    public String getArbpl();// WorkCenter

    public BigDecimal getWorkhour();
}

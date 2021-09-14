/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import java.math.BigDecimal;

/**
 *
 * @author Wei.Cheng
 */
public interface WorkingHoursReport {

    public String getDateField();

    public int getQuantity();

    public BigDecimal getSapWorktime();

    public BigDecimal getWorktimeEstimated();
    
    public BigDecimal getSapOutputValue();

    public BigDecimal getOutputValueEstimated();
    
    public String getPlant();
    

}

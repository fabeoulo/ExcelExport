/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Justin.Yeh
 */
public interface ScrappedRequisition {

    public int getId();

    public String getPo();

    public String getMaterialNumber();

    public String getModelName();

    public Integer getFloorIdBoth();

    public Integer getStateId();

    public Integer getReasonId();

    public Integer getTypeId();

    public Integer getUserId();

    public BigDecimal getUnitPrice();

    public int getAmount();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    public Date getReturnDate();

    public Integer getWeek();

    public String getReturnReason();

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.util.List;

/**
 *
 * @author Justin.Yeh
 */
public class AddRequisitionDto {

    @JsonProperty("jobNo")
    private String jobnumber = "";
    private String po = "";
    private int floorId;
    @JsonProperty("lists")
    private List<RequisitionDto> requitionDto = Lists.newArrayList(new RequisitionDto());

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public int getFloorId() {
        return floorId;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public List<RequisitionDto> getRequitionDto() {
        return requitionDto;
    }

    public void setRequitionDto(List<RequisitionDto> requitionDto) {
        this.requitionDto = requitionDto;
    }

    public static class RequisitionDto {

        @JsonProperty("materialNo")
        private String materialNumber = "";
        private int amount;
        private String remark = "";
        @JsonProperty("reasonId")
        private int requisitionReasonId;

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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getRequisitionReasonId() {
            return requisitionReasonId;
        }

        public void setRequisitionReasonId(int requisitionReasonId) {
            this.requisitionReasonId = requisitionReasonId;
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 *
 * @author Justin.Yeh
 */
public class AddRequisitionDto {

    @JsonProperty("jobNo")
    @ApiModelProperty(example = "000000000")
    private String jobnumber = "";
    private String po = "";
    private int floorId;
    @ApiModelProperty(value = "optional", required = false)
    private String agent = "RepairAI";
    
    @JsonProperty("lists")
    private List<AddRequisitionDetailDto> requitionDto = Lists.newArrayList(new AddRequisitionDetailDto());

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

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public List<AddRequisitionDetailDto> getRequitionDto() {
        return requitionDto;
    }

    public void setRequitionDto(List<AddRequisitionDetailDto> requitionDto) {
        this.requitionDto = requitionDto;
    }

    // must be static
    public static class AddRequisitionDetailDto {

        @JsonProperty("materialNo")
        @ApiModelProperty(position = 0)
        private String materialNumber = "";

        @ApiModelProperty(position = 1)
        private int amount;

        @ApiModelProperty(position = 3, example = "Emptyable")
        private String remark = "";

        @JsonProperty("reasonId")
        @ApiModelProperty(position = 2)
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

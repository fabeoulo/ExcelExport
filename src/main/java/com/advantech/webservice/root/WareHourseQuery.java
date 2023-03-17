/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice.root;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Justin.Yeh
 */
public class WareHourseQuery {
//只有當場區、工單、料號能正確查出庫存，才卡數量不能大於庫存
//否則一律不卡
    
    @JsonProperty("PLANT")
    String plant = "TWM3";

    @JsonProperty("NO")
    String no = "";

    @JsonProperty("SYS_TYPE")
    String sysType = "A";

    @JsonProperty("AUFNR_LIST")
    List<RequitionDetail> requitions;

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public List<RequitionDetail> getRequitions() {
        return requitions;
    }

    public void setRequitions(List<RequitionDetail> requitions) {
        this.requitions = requitions;
    }

    public static class RequitionDetail {

        @JsonProperty("AUFNR")
        String po;

        @JsonProperty("MATNR")
        String materialNo;

        @JsonProperty("MOVEMENT_TYPE")
        String mvt = "261";

        @JsonProperty("BDMNGS")
        int requireQty; // 可以到小數後3位(0.001) 

        @JsonProperty("REASON")
        String reason;  //NULLable

        @JsonProperty("APPLYINFO")
        String jobnumber;  //NULLable

        @JsonProperty("USER_INFO")
        String userName;  //NULLable

        public String getPo() {
            return po;
        }

        public void setPo(String po) {
            this.po = po;
        }

        public String getMaterialNo() {
            return materialNo;
        }

        public void setMaterialNo(String materialNo) {
            this.materialNo = materialNo;
        }

        public String getMvt() {
            return mvt;
        }

        public void setMvt(String mvt) {
            this.mvt = mvt;
        }

        public int getRequireQty() {
            return requireQty;
        }

        public void setRequireQty(int requireQty) {
            this.requireQty = requireQty;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getJobnumber() {
            return jobnumber;
        }

        public void setJobnumber(String jobnumber) {
            this.jobnumber = jobnumber;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

    }
}

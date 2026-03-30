/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "ScrappedDetail")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ScrappedDetail implements Serializable {

    private int id;
    private String po;
    private String modelName;
    private String materialNumber;
    private int amount;
    private String reason;
    private String kind;
    private int price;
    private String negligenceUser;
    private String remark;
    private Date createDate;
    private Floor floor;
    private int requisitionId;
    private int yk;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "po", length = 50, nullable = false)
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    @Column(name = "modelName", length = 50, nullable = false)
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Column(name = "materialNumber", length = 50, nullable = false)
    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    @NotNull
    @Column(name = "amount", nullable = false)
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Column(name = "reason", length = 200)
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "kind", length = 50)
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @NotNull
    @Column(name = "price", nullable = false)
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Column(name = "negligenceUser", length = 50, nullable = false)
    public String getNegligenceUser() {
        return negligenceUser;
    }

    public void setNegligenceUser(String negligenceUser) {
        this.negligenceUser = negligenceUser;
    }

    @Column(name = "remark", length = 200)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createDate", length = 23)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @NotNull
    @Column(name = "requisition_id", nullable = false)
    public int getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(int requisitionId) {
        this.requisitionId = requisitionId;
    }

    @NotNull
    @Column(name = "yk", nullable = false)
    public int getYk() {
        return yk;
    }

    public void setYk(int yk) {
        this.yk = yk;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.po);
        hash = 29 * hash + Objects.hashCode(this.modelName);
        hash = 29 * hash + Objects.hashCode(this.materialNumber);
        hash = 29 * hash + this.amount;
        hash = 29 * hash + Objects.hashCode(this.reason);
        hash = 29 * hash + Objects.hashCode(this.kind);
        hash = 29 * hash + this.price;
        hash = 29 * hash + Objects.hashCode(this.negligenceUser);
        hash = 29 * hash + Objects.hashCode(this.remark);
        hash = 29 * hash + Objects.hashCode(this.createDate);
        hash = 29 * hash + Objects.hashCode(this.floor);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        //System.out.println();
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ScrappedDetail other = (ScrappedDetail) obj;
        //System.out.printf(" This id: %d, other: %d ", this.id, other.id);
        if (this.amount != other.amount) {
            //System.out.printf(" This amound: %d, other: %d ", this.amount, other.amount);
            return false;
        }
        if (this.price != other.price) {
            //System.out.printf(" This price: %d, other: %d ", this.price, other.price);
            return false;
        }
        if (!Objects.equals(this.po, other.po)) {
            //System.out.printf(" This po: %s, other: %s ", this.po, other.po);
            return false;
        }
        if (!Objects.equals(this.modelName, other.modelName)) {
            //System.out.printf(" This modelName: %s, other: %s ", this.modelName, other.modelName);
            return false;
        }
        if (!Objects.equals(this.materialNumber, other.materialNumber)) {
            //System.out.printf(" This materialNumber: %s, other: %s ", this.materialNumber, other.materialNumber);
            return false;
        }
        if (!Objects.equals(this.reason, other.reason)) {
            //System.out.printf(" This reason: %s, other: %s ", this.reason, other.reason);
            return false;
        }
        if (!Objects.equals(this.kind, other.kind)) {
            //System.out.printf(" This kind: %s, other: %s ", this.kind, other.kind);
            return false;
        }
        if (!Objects.equals(this.negligenceUser, other.negligenceUser)) {
            //System.out.printf(" This negligenceUser: %s, other: %s ", this.negligenceUser, other.negligenceUser);
            return false;
        }
        if (!Objects.equals(this.remark, other.remark)) {
            //System.out.printf(" This remark: %s, other: %s ", this.remark, other.remark);
            return false;
        }
        if (this.createDate == null && other.createDate == null) {
            return true;
        }
        if ((this.createDate == null && other.createDate != null)
                || (this.createDate != null && other.createDate == null)
                || !Objects.equals(this.createDate.getTime(), other.createDate.getTime())) {
            //System.out.printf(" This createDate: %s, other: %s ", this.createDate.toString(), other.createDate.toString());
            return false;
        }
        if (!Objects.equals(this.floor, other.floor)) {
            //System.out.printf(" This floor: %s, other: %s ", this.floor == null ? "null" : this.floor.toString(), other.floor == null ? "null" : other.floor.toString());
            return false;
        }
        return true;
    }

}

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
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "Requisition_Event")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class RequisitionEvent implements Serializable {

    private int id;
    private Requisition requisition;
    private User user;
    private RequisitionState requisitionState;
    private Date modifiedDate;
    private String remark;
    private RequisitionReason requisitionReason;
    private RequisitionType requisitionType;

    public RequisitionEvent() {
    }

    public RequisitionEvent(Requisition requisition, User user, RequisitionState requisitionState, String remark, RequisitionReason requisitionReason, RequisitionType requisitionType) {
        this.requisition = requisition;
        this.user = user;
        this.requisitionState = requisitionState;
        this.remark = remark;
        this.requisitionReason = requisitionReason;
        this.requisitionType = requisitionType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id")
    public Requisition getRequisition() {
        return requisition;
    }

    public void setRequisition(Requisition requisition) {
        this.requisition = requisition;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionState_id")
    public RequisitionState getRequisitionState() {
        return requisitionState;
    }

    public void setRequisitionState(RequisitionState requisitionState) {
        this.requisitionState = requisitionState;
    }

    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", length = 23)
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Column(name = "remark", length = 50)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionReason_id")
    public RequisitionReason getRequisitionReason() {
        return requisitionReason;
    }

    public void setRequisitionReason(RequisitionReason requisitionReason) {
        this.requisitionReason = requisitionReason;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisitionType_id")
    public RequisitionType getRequisitionType() {
        return requisitionType;
    }

    public void setRequisitionType(RequisitionType requisitionType) {
        this.requisitionType = requisitionType;
    }
 
}

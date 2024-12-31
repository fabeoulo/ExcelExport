/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.model.db1;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Justin.Yeh
 */
@Entity
@Table(name = "vw_M3_Worktime")
public class VwM3Worktime implements java.io.Serializable {

    private int id;
    private String modelName;
    private String speMail;
    private String bpeMail;
    private String qcMail;

    @Id
    @Column(name = "rowId", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "modelName", length = 50, nullable = false)
    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Column(name = "speMail", length = 100)
    public String getSpeMail() {
        return speMail;
    }

    public void setSpeMail(String speMail) {
        this.speMail = speMail;
    }

    @Column(name = "bpeMail", length = 100)
    public String getBpeMail() {
        return bpeMail;
    }

    public void setBpeMail(String bpeMail) {
        this.bpeMail = bpeMail;
    }

    @Column(name = "qcMail", length = 100)
    public String getQcMail() {
        return qcMail;
    }

    public void setQcMail(String qcMail) {
        this.qcMail = qcMail;
    }

}

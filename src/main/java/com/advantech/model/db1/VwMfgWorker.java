/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Justin.Yeh
 */
@Entity
@Table(name = "vw_Mfg_Worker")
public class VwMfgWorker implements java.io.Serializable {

    private int id;
    private String jobnumber;
    private String username;
    private String workclass;
    private String station;
    private String op;
    private Date onjobdate;
    private String onjob;
    private String area;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "jobNo", length = 10, nullable = false)
    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    @Column(name = "name", length = 50, nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "workclass", length = 50)
    public String getWorkclass() {
        return workclass;
    }

    public void setWorkclass(String workclass) {
        this.workclass = workclass;
    }

    @Column(name = "station", length = 20)
    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Column(name = "op", length = 10, nullable = false)
    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "onjobdate", length = 23, updatable = false)
    public Date getOnjobdate() {
        return onjobdate;
    }

    public void setOnjobdate(Date onjobdate) {
        this.onjobdate = onjobdate;
    }

    @Column(name = "onjob", length = 50)
    public String getOnjob() {
        return onjob;
    }

    public void setOnjob(String onjob) {
        this.onjob = onjob;
    }

    @Column(name = "area", length = 10)
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}

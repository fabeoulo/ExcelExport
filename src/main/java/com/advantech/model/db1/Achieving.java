/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Justin.Yeh
 */
@Entity
@Table(name = "Achieving")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Achieving implements Serializable {

    private int id;
    private BigDecimal outputValueEstimated = BigDecimal.ONE;
    private BigDecimal worktimeEstimated = BigDecimal.ONE;
    private String factory;
    private String sapProductionType;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "outputValue_estimated", precision = 10, scale = 1)
    public BigDecimal getOutputValueEstimated() {
        return outputValueEstimated;
    }

    public void setOutputValueEstimated(BigDecimal outputValueEstimated) {
        this.outputValueEstimated = outputValueEstimated;
    }

    @Column(name = "worktime_estimated", precision = 10, scale = 1)
    public BigDecimal getWorktimeEstimated() {
        return worktimeEstimated;
    }

    public void setWorktimeEstimated(BigDecimal worktimeEstimated) {
        this.worktimeEstimated = worktimeEstimated;
    }

    @Column(name = "factory")
    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    @Column(name = "sap_production_type")
    public String getSapProductionType() {
        return sapProductionType;
    }

    public void setSapProductionType(String sapProductionType) {
        this.sapProductionType = sapProductionType;
    }

}

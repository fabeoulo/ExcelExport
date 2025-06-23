/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Justin.Yeh
 */
@Entity
@Table(name = "Requisition_CateMes")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class RequisitionCateMes implements Serializable {

    private int id;
    private String name;

    @JsonIgnore
    private Set<Requisition> requisitions = new HashSet(0);

    @JsonIgnore
    private Set<RequisitionEvent> requisitionEvents = new HashSet(0);

    private Set<RequisitionCateIms> requisitionCateImss = new HashSet(0);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "[name]", length = 100, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "requisitionCateMes")
    public Set<Requisition> getRequisitions() {
        return requisitions;
    }

    public void setRequisitions(Set<Requisition> requisitions) {
        this.requisitions = requisitions;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "requisitionCateMes")
    public Set<RequisitionEvent> getRequisitionEvents() {
        return requisitionEvents;
    }

    public void setRequisitionEvents(Set<RequisitionEvent> requisitionEvents) {
        this.requisitionEvents = requisitionEvents;
    }

    @ManyToMany(mappedBy = "requisitionCateMess")
    public Set<RequisitionCateIms> getRequisitionCateImss() {
        return requisitionCateImss;
    }

    public void setRequisitionCateImss(Set<RequisitionCateIms> requisitionCateImss) {
        this.requisitionCateImss = requisitionCateImss;
    }

}

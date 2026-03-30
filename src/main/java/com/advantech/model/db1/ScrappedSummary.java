/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
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
@Table(name = "ScrappedSummary")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ScrappedSummary {

    private int id;
    private int yk;
    private String area;
    private int ScrapSumHundredUp;
    private int ScrapPcsHundredUp;
    private int ScrapSumHundredDown;
    private int ScrapPcsHundredDown;
    private int ShortSumHundredUp;
    private int ShortPcsHundredUp;
    private int ShortSumHundredDown;
    private int ShortPcsHundredDown;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "yk", nullable = false)
    public int getYk() {
        return yk;
    }

    public void setYk(int yk) {
        this.yk = yk;
    }

    @Column(name = "area", length = 10, nullable = false)
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Column(name = "ScrapSumHundredUp", nullable = false)
    public int getScrapSumHundredUp() {
        return ScrapSumHundredUp;
    }

    public void setScrapSumHundredUp(int ScrapSumHundredUp) {
        this.ScrapSumHundredUp = ScrapSumHundredUp;
    }

    @Column(name = "ScrapSumHundredDown", nullable = false)
    public int getScrapSumHundredDown() {
        return ScrapSumHundredDown;
    }

    public void setScrapSumHundredDown(int ScrapSumHundredDown) {
        this.ScrapSumHundredDown = ScrapSumHundredDown;
    }

    @Column(name = "ShortSumHundredUp", nullable = false)
    public int getShortSumHundredUp() {
        return ShortSumHundredUp;
    }

    public void setShortSumHundredUp(int ShortSumHundredUp) {
        this.ShortSumHundredUp = ShortSumHundredUp;
    }

    @Column(name = "ShortSumHundredDown", nullable = false)
    public int getShortSumHundredDown() {
        return ShortSumHundredDown;
    }

    public void setShortSumHundredDown(int ShortSumHundredDown) {
        this.ShortSumHundredDown = ShortSumHundredDown;
    }

    @Column(name = "ScrapPcsHundredUp", nullable = false)
    public int getScrapPcsHundredUp() {
        return ScrapPcsHundredUp;
    }

    public void setScrapPcsHundredUp(int ScrapPcsHundredUp) {
        this.ScrapPcsHundredUp = ScrapPcsHundredUp;
    }

    @Column(name = "ScrapPcsHundredDown", nullable = false)
    public int getScrapPcsHundredDown() {
        return ScrapPcsHundredDown;
    }

    public void setScrapPcsHundredDown(int ScrapPcsHundredDown) {
        this.ScrapPcsHundredDown = ScrapPcsHundredDown;
    }

    @Column(name = "ShortPcsHundredUp", nullable = false)
    public int getShortPcsHundredUp() {
        return ShortPcsHundredUp;
    }

    public void setShortPcsHundredUp(int ShortPcsHundredUp) {
        this.ShortPcsHundredUp = ShortPcsHundredUp;
    }

    @Column(name = "ShortPcsHundredDown", nullable = false)
    public int getShortPcsHundredDown() {
        return ShortPcsHundredDown;
    }

    public void setShortPcsHundredDown(int ShortPcsHundredDown) {
        this.ShortPcsHundredDown = ShortPcsHundredDown;
    }

}

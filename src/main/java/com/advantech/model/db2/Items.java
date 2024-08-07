package com.advantech.model.db2;
// Generated 2020/7/1 下午 05:20:27 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Items generated by hbm2java
 */
@Entity
@Table(name = "items",
        catalog = "lacking"
)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Items implements java.io.Serializable {

    private Integer id;
    private Orders orders;
    private String label1;
    private String label2;
    private String label3;
    private String label4 = "";
    private Boolean mrpSync = false;
    private String mrpCode = "";

    public Items() {
    }

    public Items(Orders orders, String label1, String label2, String label3, String label4) {
        this.orders = orders;
        this.label1 = label1;
        this.label2 = label2;
        this.label3 = label3;
        this.label4 = label4;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Orders getOrders() {
        return this.orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    @Column(name = "label_1", nullable = false, length = 20)
    public String getLabel1() {
        return this.label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    @Column(name = "label_2", nullable = false, length = 20)
    public String getLabel2() {
        return this.label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    @Column(name = "label_3", nullable = false, length = 20)
    public String getLabel3() {
        return this.label3;
    }

    public void setLabel3(String label3) {
        this.label3 = label3;
    }

    @Column(name = "label_4", length = 20, nullable = false)
    public String getLabel4() {
        return label4;
    }

    public void setLabel4(String label4) {
        this.label4 = label4;
    }

    @Column(name = "mrp_sync", nullable = false)
    public Boolean getMrpSync() {
        return mrpSync;
    }

    public void setMrpSync(Boolean mrpSync) {
        this.mrpSync = mrpSync;
    }

    @Column(name = "mrp_code", length = 3, nullable = false)
    public String getMrpCode() {
        return mrpCode;
    }

    public void setMrpCode(String mrpCode) {
        this.mrpCode = mrpCode;
    }
}

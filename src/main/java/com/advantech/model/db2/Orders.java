package com.advantech.model.db2;
// Generated 2020/7/1 下午 05:20:27 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Orders generated by hbm2java
 */
@Entity
@Table(name = "orders",
        catalog = "lacking"
)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Orders implements java.io.Serializable {

    private Integer id;
    private OrderTypes orderTypes;
    private Teams teams;
    private Users users;
    private int number;
    private OrderResponse orderResponse;
    private String ownerId;
    private String comment;
    private Date timeOpen;
    private Date timeClose;
    private Date respectDate;
    private Set<Items> itemses = new HashSet<Items>(0);

    public Orders() {
    }

    public Orders(Teams teams, Users users, int number, Date timeOpen, Date timeClose) {
        this.teams = teams;
        this.users = users;
        this.number = number;
        this.timeOpen = timeOpen;
        this.timeClose = timeClose;
    }

    public Orders(OrderTypes orderTypes, Teams teams, Users users, int number, String comment, Date timeOpen, Date timeClose, Date respectDate, Set<Items> itemses) {
        this.orderTypes = orderTypes;
        this.teams = teams;
        this.users = users;
        this.number = number;
        this.comment = comment;
        this.timeOpen = timeOpen;
        this.timeClose = timeClose;
        this.respectDate = respectDate;
        this.itemses = itemses;
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
    @JoinColumn(name = "type")
    public OrderTypes getOrderTypes() {
        return this.orderTypes;
    }

    public void setOrderTypes(OrderTypes orderTypes) {
        this.orderTypes = orderTypes;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    public Teams getTeams() {
        return this.teams;
    }

    public void setTeams(Teams teams) {
        this.teams = teams;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public Users getUsers() {
        return this.users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Column(name = "number", nullable = false)
    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id")
    public OrderResponse getOrderResponse() {
        return orderResponse;
    }

    public void setOrderResponse(OrderResponse orderResponse) {
        this.orderResponse = orderResponse;
    }

    @Column(name = "owner_id", length = 10)
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Column(name = "comment", length = 65535)
    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_open", nullable = true, length = 19)
    public Date getTimeOpen() {
        return this.timeOpen;
    }

    public void setTimeOpen(Date timeOpen) {
        this.timeOpen = timeOpen;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_close", nullable = true, length = 19)
    public Date getTimeClose() {
        return this.timeClose;
    }

    public void setTimeClose(Date timeClose) {
        this.timeClose = timeClose;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "respect_date", length = 19)
    public Date getRespectDate() {
        return this.respectDate;
    }

    public void setRespectDate(Date respectDate) {
        this.respectDate = respectDate;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orders")
    public Set<Items> getItemses() {
        return this.itemses;
    }

    public void setItemses(Set<Items> itemses) {
        this.itemses = itemses;
    }

}

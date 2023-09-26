package com.advantech.model.db2;
// Generated 2020/7/1 下午 05:20:27 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Teams generated by hbm2java
 */
@Entity
@Table(name = "teams",
         catalog = "lacking"
)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Teams implements java.io.Serializable {

    private Integer id;
    private String name;
    private Integer permission;
    private String nickname;
    private String plant;
    private Set<Orders> orderses = new HashSet<Orders>(0);
    private Set<Users> userses = new HashSet<Users>(0);
    private Set<Replies> replieses = new HashSet<Replies>(0);

    public Teams() {
    }

    public Teams(String name) {
        this.name = name;
    }

    public Teams(String name, Integer permission, String nickname, Set<Orders> orderses, Set<Users> userses) {
        this.name = name;
        this.permission = permission;
        this.nickname = nickname;
        this.orderses = orderses;
        this.userses = userses;
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

    @Column(name = "name", nullable = false, length = 20)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "permission")
    public Integer getPermission() {
        return this.permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    @Column(name = "nickname", length = 20)
    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Column(name = "plant", length = 4)
    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "teams")
    public Set<Orders> getOrderses() {
        return this.orderses;
    }

    public void setOrderses(Set<Orders> orderses) {
        this.orderses = orderses;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "teams")
    public Set<Users> getUserses() {
        return this.userses;
    }

    public void setUserses(Set<Users> userses) {
        this.userses = userses;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orders")
    public Set<Replies> getReplieses() {
        return this.replieses;
    }

    public void setReplieses(Set<Replies> replieses) {
        this.replieses = replieses;
    }
}

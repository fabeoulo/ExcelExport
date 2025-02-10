/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, CrudRepository<User, Integer> {

    public User findByJobnumber(String jobnumber);

    public List<User> findByUserNotifications(UserNotification notifi);

    @Procedure("usp_QuickInsertUserWithName")
    public void saveUserWithNameByProc(String jobnumber, String email, String name);
}

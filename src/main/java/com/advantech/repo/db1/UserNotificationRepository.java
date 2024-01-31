/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.UserNotification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    public UserNotification findByName(String name);

    @Query("SELECT u FROM UserNotification AS u JOIN FETCH u.users WHERE u.id = ?1")
    public Optional<UserNotification> findByIdWithUser(Integer id);

    @Query("SELECT u FROM UserNotification AS u JOIN FETCH u.users WHERE u.name = ?1")
    public Optional<UserNotification> findByNameWithUser(String name);
}

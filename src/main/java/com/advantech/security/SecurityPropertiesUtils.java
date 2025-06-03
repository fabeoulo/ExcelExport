/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.security;

import com.advantech.model.db1.User;
import static com.google.common.base.Preconditions.checkState;
import java.util.Collection;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Wei.Cheng
 */
public class SecurityPropertiesUtils {

    public static User retrieveAndCheckUserInSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkState(!(auth instanceof AnonymousAuthenticationToken), "查無登入紀錄，請重新登入");
        return (User) auth.getPrincipal();
    }

    public static boolean checkUserInAuthorities(User user, Collection<? extends GrantedAuthority> autho) {
        return user.getAuthorities().stream().anyMatch(u -> autho.contains(u));
    }

    public static void loginUserManual(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("User manually logged in: " + ((User) userDetails).getJobnumber());
    }

    public static void logoutUserManual() {
        SecurityContextHolder.clearContext();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.User;
import com.advantech.security.State;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.advantech.api.WebApiClient;
import com.advantech.api.WebApiUser;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Wei.Cheng
 */
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;
	
    @Autowired
    @Qualifier("webApiClient")
    private WebApiClient wc;

    @Override
    public UserDetails loadUserByUsername(String jobnumber) throws UsernameNotFoundException {
        User user = userService.findByJobnumber(jobnumber);
        if (user == null) {
            WebApiUser atmcUser = wc.getUserInAtmc(jobnumber);
            if (atmcUser != null && atmcUser.Active == 1) {
                userService.saveUserWithNameByProc(atmcUser.Emplr_Id, atmcUser.Email_Addr, atmcUser.Local_Name);
                user = userService.findByJobnumber(jobnumber);
            } else {
                System.out.println("User not found");
                throw new UsernameNotFoundException("Username not found");
            }
        }

        user.addSecurityInfo(true, true, true, true, getGrantedAuthorities(user));
        return user;
    }

    private List<GrantedAuthority> getGrantedAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        user.getUserProfiles().forEach((userProfile) -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getName()));
        });

        System.out.println("authorities :" + authorities);
        return authorities;
    }

}

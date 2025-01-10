package com.ecommerce.service.impl;

import com.ecommerce.enums.USER_ROLE;
import com.ecommerce.model.Seller;
import com.ecommerce.model.User;
import com.ecommerce.repository.SellerRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final String SELLER_PREFIX = "seller_";


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        if (username.startsWith(SELLER_PREFIX)) {
            String actualUserName = username.substring(SELLER_PREFIX.length());
            Seller seller = sellerRepository.findByEmail(actualUserName);
            if (seller != null) {
                return buildUserDetails(seller.getEmail(), seller.getPassword(), seller.getRole());
            }

        } else {
            User user = userRepository.findByEmail(username);
            if (user != null) {
                return buildUserDetails(user.getEmail(), user.getPassword(), user.getRole());
            }
        }
        throw new UsernameNotFoundException("User not available");
    }

    private UserDetails buildUserDetails(String email, String password, USER_ROLE role) {

        if (role == null) role = USER_ROLE.USER_CUSTOMER;

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(role.toString()));
        return new org.springframework.security.core.userdetails.User(email, password, grantedAuthorities);
    }


}

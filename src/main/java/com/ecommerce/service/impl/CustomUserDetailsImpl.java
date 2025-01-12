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

import static com.ecommerce.staticVariables.StaticVariables.SELLER_PREFIX;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("debugging: 1: username i sent " + username);

        if (username.startsWith(SELLER_PREFIX)) {
            String actualUserName = username.substring(SELLER_PREFIX.length());
            System.out.println("username i moditifed to " + actualUserName);
            Seller seller = sellerRepository.findByEmail(actualUserName);
            System.out.println("seller it retrieved" + seller.toString());
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

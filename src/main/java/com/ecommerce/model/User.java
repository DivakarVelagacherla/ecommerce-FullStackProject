package com.ecommerce.model;

import com.ecommerce.enums.USER_ROLE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private USER_ROLE role = USER_ROLE.USER_CUSTOMER;
    @OneToMany
    private Set<Address> addresses = new HashSet<>();
    @ManyToMany
    @JsonIgnore
    private Set<Coupon> couponsUsed = new HashSet<>();


}

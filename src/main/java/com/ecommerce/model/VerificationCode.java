package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String OTP;

    private String email;

    @OneToOne
    private User user;
    
    @OneToOne
    private Seller seller;
}

package com.ecommerce.repository;

import com.ecommerce.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    VerificationCode findByEmail(String username);

    VerificationCode findByOTP(String otp);
}

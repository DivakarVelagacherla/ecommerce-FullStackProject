package com.ecommerce.service.impl;

import com.ecommerce.config.JWT_CONSTANT;
import com.ecommerce.config.JwtProvider;
import com.ecommerce.enums.USER_ROLE;
import com.ecommerce.model.Cart;
import com.ecommerce.model.Seller;
import com.ecommerce.model.User;
import com.ecommerce.model.VerificationCode;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.SellerRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.VerificationCodeRepository;
import com.ecommerce.request.SignInRequest;
import com.ecommerce.response.AuthResponse;
import com.ecommerce.response.SignUpRequest;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.EmailService;
import com.ecommerce.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ecommerce.staticVariables.StaticVariables.SELLER_PREFIX;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserDetailsImpl userDetailsService;
    private final SellerRepository sellerRepository;

    @Override
    public String createUser(SignUpRequest signUpRequest) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(signUpRequest.getEmail());

        if (verificationCode != null && !verificationCode.getOTP().equals(signUpRequest.getOtp())) {
            throw new Exception("Wrong OTP..");
        }

        User user = userRepository.findByEmail(signUpRequest.getEmail());
        System.out.println("checking if user already exists" + user);
        if (user == null) {
            User createdUser = new User();
            createdUser.setEmail(signUpRequest.getEmail());
            createdUser.setFirstName(signUpRequest.getFirstName());
            createdUser.setLastName(signUpRequest.getLastName());
            createdUser.setRole(USER_ROLE.USER_CUSTOMER);
            createdUser.setMobileNumber("5125668620");
            createdUser.setPassword(passwordEncoder.encode(signUpRequest.getOtp()));

            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.USER_CUSTOMER.toString()));
            Authentication authentication = new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return JWT_CONSTANT.JWT_KEY + jwtProvider.generateToken(authentication);
        } else {
            return JWT_CONSTANT.USER_EXISTS + "User already exists";
        }

    }

    @Override
    public AuthResponse signIn(SignInRequest signInRequest) throws Exception {
        System.out.println("details i passed: " + signInRequest);

        String email = signInRequest.getEmail();
        String otp = signInRequest.getOtp();

        System.out.println(email + " " + otp);

        Authentication authentication = authenticate(email, otp);
        String jwt = jwtProvider.generateToken(authentication);
        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        String role = grantedAuthorities.isEmpty() ? null : grantedAuthorities.iterator().next().getAuthority();

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login Successful");
        authResponse.setRole(USER_ROLE.valueOf(role));
        return authResponse;
    }

    private Authentication authenticate(String email, String otp) throws Exception {

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }

        if (email.startsWith(SELLER_PREFIX)) {
            email = email.substring(SELLER_PREFIX.length());
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
        if (verificationCode == null || !verificationCode.getOTP().equals(otp)) {
            throw new BadCredentialsException("Invalid otp");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public void sendLoginOtp(String username) throws Exception {

        if (username != null) {

            if (username.startsWith(SELLER_PREFIX)) {
                String email = username.substring(SELLER_PREFIX.length());
                Seller seller = sellerRepository.findByEmail(email);
                if (seller == null) {
                    throw new Exception("Seller not available with email: " + email);
                }
                VerificationCode isExist = verificationCodeRepository.findByEmail(email);
                if (isExist != null) {
                    verificationCodeRepository.delete(isExist);
                }
            } else {
                User user = userRepository.findByEmail(username);
                if (user == null) {
                    throw new Exception("User not available with provider");
                }
                VerificationCode isExist = verificationCodeRepository.findByEmail(username);
                if (isExist != null) {
                    verificationCodeRepository.delete(isExist);
                }
            }


            String otp = OtpUtil.generateOtp();
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setOTP(otp);
            String email = username.startsWith(SELLER_PREFIX) ? username.substring(SELLER_PREFIX.length()) : username;
            verificationCode.setEmail(email);
            verificationCodeRepository.save(verificationCode);

            String subject = "DIVAKAR ECOMMERCE - OPT REQUEST";
            String body = "You have requested for OPT. OTP is - " + otp;
            emailService.sendOtpEmail(email, subject, body);

        }


    }


}

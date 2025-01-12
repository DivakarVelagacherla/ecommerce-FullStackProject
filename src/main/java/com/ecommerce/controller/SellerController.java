package com.ecommerce.controller;

import com.ecommerce.config.JwtProvider;
import com.ecommerce.enums.AccountStatus;
import com.ecommerce.model.Seller;
import com.ecommerce.model.VerificationCode;
import com.ecommerce.repository.VerificationCodeRepository;
import com.ecommerce.request.SignInRequest;
import com.ecommerce.response.AuthResponse;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.EmailService;
import com.ecommerce.service.SellerService;
import com.ecommerce.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ecommerce.staticVariables.StaticVariables.EMAIL_VERIFICATION_LINK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginSeller(@RequestBody SignInRequest request) throws Exception {
//        String otp = request.getOtp();
//        String email = request.getEmail();
        System.out.println("details i sent" + request);

        AuthResponse authResponse = authService.signIn(request);

        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByOTP(otp);

        if (verificationCode == null || !verificationCode.getOTP().equals(otp)) {
            throw new Exception("Wrong otp..");
        }
        Seller seller = sellerService.verifySellerEmail(verificationCode.getEmail(), otp);
        return ResponseEntity.ok(seller);
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws Exception {

        Seller savedSeller = sellerService.createSeller(seller);
        String otp = OtpUtil.generateOtp();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(savedSeller.getEmail());
        verificationCode.setOTP(otp);
        verificationCodeRepository.save(verificationCode);

        String subject = "Verfication email for the Seller Account";
        String body = "Good Day,\n\nThank you for your business with us. In order to verify your Seller Account, you need to verify your account with the following link: \n" + EMAIL_VERIFICATION_LINK + "\n\nOnce again thank you for your business with us";
        emailService.sendOtpEmail(savedSeller.getEmail(), subject, body);
        return new ResponseEntity<>(savedSeller, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws Exception {
        Seller seller = sellerService.getSellerById(id);
        return ResponseEntity.ok(seller);
    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(@RequestHeader("Authorization") String jwt) throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);
        return ResponseEntity.ok(seller);
    }

//    public ResponseEntity<SellerReport> getSellerReport()

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers(@RequestParam(required = false) AccountStatus status) {
        List<Seller> allSellers = sellerService.getAllSellers(status);
        return ResponseEntity.ok(allSellers);
    }

    @PatchMapping
    public ResponseEntity<Seller> updateSeller(@RequestHeader("Authorization") String jwt, @RequestBody Seller seller) throws Exception {
        Seller sellerProfile = sellerService.getSellerProfile(jwt);
        Seller updateSeller = sellerService.updateSeller(sellerProfile.getId(), sellerProfile);
        return ResponseEntity.ok(updateSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Seller> deleteSeller(@PathVariable Long id) throws Exception {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}

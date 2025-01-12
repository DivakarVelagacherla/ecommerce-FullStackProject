package com.ecommerce.service.impl;

import com.ecommerce.config.JwtProvider;
import com.ecommerce.enums.AccountStatus;
import com.ecommerce.enums.USER_ROLE;
import com.ecommerce.model.Address;
import com.ecommerce.model.Seller;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.SellerRepository;
import com.ecommerce.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return this.getSellerByEmail(email);
    }

    @Override
    public Seller createSeller(Seller seller) throws Exception {
        Seller sellerInDb = sellerRepository.findByEmail(seller.getEmail());
        if (sellerInDb != null) {
            throw new Exception("Seller already present");
        }

        Address pickUpAddress = addressRepository.save(seller.getPickupAddress());
        Seller newSeller = new Seller();
        newSeller.setEmail(seller.getEmail());
        newSeller.setSellerName(seller.getSellerName());
        newSeller.setPassword(passwordEncoder.encode(seller.getPassword()));
        newSeller.setRole(USER_ROLE.USER_SELLER);
        newSeller.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        newSeller.setPickupAddress(pickUpAddress);
        newSeller.setGSTIN(seller.getGSTIN());
        newSeller.setMobileNumber(seller.getMobileNumber());
        newSeller.setBusinessDetails(seller.getBusinessDetails());
        newSeller.setBankDetails(seller.getBankDetails());
        return sellerRepository.save(newSeller);
    }

    @Override
    public Seller getSellerById(Long id) throws Exception {
        return sellerRepository.findById(id).orElseThrow(() -> new Exception("Seller not available at id: " + id));
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            throw new Exception("Seller not available");
        }
        return seller;
    }

    @Override
    public List<Seller> getAllSellers(AccountStatus status) {
        return sellerRepository.findByAccountStatus(status);
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) throws Exception {

        Seller existingSeller = this.getSellerById(id);

        if (seller.getEmail() != null) {
            existingSeller.setEmail(seller.getEmail());
        }


        if (seller.getSellerName() != null) {
            existingSeller.setSellerName(seller.getSellerName());
        }


        if (seller.getMobileNumber() != null) {
            existingSeller.setMobileNumber(seller.getMobileNumber());
        }
        if (seller.getBusinessDetails() != null && seller.getBusinessDetails().getBusinessName() != null) {
            existingSeller.getBusinessDetails().setBusinessName(seller.getBusinessDetails().getBusinessName());
        }
        if (seller.getBankDetails() != null
                && seller.getBankDetails().getAccountHolderName() != null
                && seller.getBankDetails().getIfscCode() != null
                && seller.getBankDetails().getAccountNumber() != null
        ) {
            existingSeller.getBankDetails().setAccountHolderName(
                    seller.getBankDetails().getAccountHolderName()
            );
            existingSeller.getBankDetails().setAccountNumber(
                    seller.getBankDetails().getAccountNumber()
            );
            existingSeller.getBankDetails().setIfscCode(
                    seller.getBankDetails().getIfscCode()
            );
        }
        if (seller.getPickupAddress() != null
                && seller.getPickupAddress().getStreet() != null
                && seller.getPickupAddress().getCity() != null
                && seller.getPickupAddress().getState() != null
                && seller.getPickupAddress().getCountry() != null
                && seller.getPickupAddress().getZipCode() != null) {

            existingSeller.getPickupAddress().setStreet(seller.getPickupAddress().getStreet());
            existingSeller.getPickupAddress().setCity(seller.getPickupAddress().getCity());
            existingSeller.getPickupAddress().setState(seller.getPickupAddress().getState());
            existingSeller.getPickupAddress().setCountry(seller.getPickupAddress().getCountry());
            existingSeller.getPickupAddress().setZipCode(seller.getPickupAddress().getZipCode());
        }

        if (seller.getGSTIN() != null) {
            existingSeller.setGSTIN(seller.getGSTIN());
        }
        return sellerRepository.save(existingSeller);

    }

    @Override
    public void deleteSeller(Long id) throws Exception {

        Seller seller = this.getSellerById(id);
        sellerRepository.delete(seller);
    }

    @Override
    public Seller verifySellerEmail(String email, String otp) throws Exception {
        Seller seller = this.getSellerByEmail(email);
        seller.setEmailVerified(true);
        seller.setAccountStatus(AccountStatus.ACTIVE);
        return seller;
    }

    @Override
    public Seller updateSellerAccountStatus(Long id, AccountStatus status) throws Exception {
        Seller seller = this.getSellerById(id);
        seller.setAccountStatus(status);
        return seller;
    }
}

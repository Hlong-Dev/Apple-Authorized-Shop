package com.example.demo.service;

import com.example.demo.model.DiscountCode;
import com.example.demo.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountCodeService {

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    public List<DiscountCode> getAllDiscountCodes() {
        return discountCodeRepository.findAll();
    }

    public void saveDiscountCode(DiscountCode discountCode) {
        discountCodeRepository.save(discountCode);
    }

    public Optional<DiscountCode> getDiscountCodeById(Long id) {
        return discountCodeRepository.findById(id);
    }

    public void updateDiscountCode(DiscountCode discountCode) {
        discountCodeRepository.save(discountCode);
    }

    public void deleteDiscountCode(Long id) {
        discountCodeRepository.deleteById(id);
    }
}

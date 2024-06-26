package com.example.demo.controller;

import com.example.demo.service.MoMoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private MoMoPaymentService moMoPaymentService;

    @GetMapping("/payment/create")
    public String createPayment(
            @RequestParam String customerName,
            @RequestParam String phoneCustomer,
            @RequestParam String addressCustomer,
            @RequestParam String emailCustomer,
            @RequestParam String descriptionOrder) {
        try {
            return moMoPaymentService.createPayment(customerName, phoneCustomer, addressCustomer, emailCustomer, descriptionOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }
}

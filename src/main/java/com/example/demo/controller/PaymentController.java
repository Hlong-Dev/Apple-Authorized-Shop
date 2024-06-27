package com.example.demo.controller;

import com.example.demo.service.CartService;
import com.example.demo.service.MoMoPaymentService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class PaymentController {

    @Autowired
    private MoMoPaymentService moMoPaymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

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

    @GetMapping("/momo_return")
    public ModelAndView momoReturn(
            @RequestParam(value = "errorCode", required = false) String errorCode) {

        ModelAndView modelAndView = new ModelAndView();
        if (errorCode == null || !"0".equals(errorCode)) {
            modelAndView.setViewName("failure");
            modelAndView.addObject("message", "Payment failed. Please try again.");
        } else {
            modelAndView.setViewName("success");
            modelAndView.addObject("message", "Payment successful!");
        }

        return modelAndView;
    }
}

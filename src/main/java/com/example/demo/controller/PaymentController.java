package com.example.demo.controller;


import com.example.demo.service.MoMoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    private MoMoPaymentService moMoPaymentService;

    @GetMapping("/payment")
    public RedirectView payment() throws Exception {
        String payUrl = moMoPaymentService.createPayment();
        return new RedirectView(payUrl);
    }

    @GetMapping("/confirmPaymentClient")
    public String confirmPaymentClient(@RequestParam("errorCode") String errorCode,
                                       @RequestParam("orderId") String orderId,
                                       @RequestParam("message") String message) {
        if ("0".equals(errorCode)) {
            // Payment success logic
            return "paymentSuccess";
        } else {
            // Payment failure logic
            return "paymentFailure";
        }
    }

    @PostMapping("/payment/notify")
    @ResponseBody
    public String paymentNotify(@RequestBody Map<String, String> payload) {
        // Xử lý thông báo từ MoMo
        String resultCode = payload.get("resultCode");
        if ("0".equals(resultCode)) {
            // Payment success logic
            return "success";
        } else {
            // Payment failure logic
            return "failure";
        }
    }
}

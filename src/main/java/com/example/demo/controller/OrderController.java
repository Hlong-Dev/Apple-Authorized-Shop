package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.service.CartService;
import com.example.demo.service.MoMoPaymentService;
import com.example.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private MoMoPaymentService moMoPaymentService;

    @Autowired
    private PaymentVnpayController paymentVnpayController;

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        model.addAttribute("total", cartService.calculateTotalPrice());
        model.addAttribute("discount", cartService.getDiscount());
        model.addAttribute("finalPrice", cartService.calculateTotalPrice() - cartService.getDiscount());
        return "order/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(
            @RequestParam("customerName") String customerName,
            @RequestParam("phoneCustomer") String phoneCustomer,
            @RequestParam("addressCustomer") String addressCustomer,
            @RequestParam("emailCustomer") String emailCustomer,
            @RequestParam("descriptionOrder") String descriptionOrder,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpServletRequest request,
            Model model) {

        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";  // Redirect if cart is empty
        }

        if ("momo".equals(paymentMethod)) {
            try {
                String payUrl = moMoPaymentService.createPayment(customerName, phoneCustomer, addressCustomer, emailCustomer, descriptionOrder);
                return "redirect:" + payUrl;
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "Error occurred: " + e.getMessage());
                return "error";
            }
        } else if ("vnpay".equals(paymentMethod)) {
            try {
                RedirectView redirectView = paymentVnpayController.createPayment(request);
                return "redirect:" + redirectView.getUrl();
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "Error occurred: " + e.getMessage());
                return "error";
            }
        } else {
            orderService.createOrder(customerName, phoneCustomer, addressCustomer, emailCustomer, descriptionOrder, cartItems);
            return "redirect:/order/confirmation";
        }
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}

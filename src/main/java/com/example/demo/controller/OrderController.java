package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.service.CartService;
import com.example.demo.service.MoMoPaymentService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final MoMoPaymentService moMoPaymentService;
    private final PaymentVnpayController paymentVnpayController;
    private final UserService userService;

    @GetMapping("/checkout")
    public String showCheckout(Model model, Authentication authentication) {
        // Get currently logged-in user's details
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("total", cartService.calculateTotalPrice());
        model.addAttribute("discount", cartService.getDiscount());
        model.addAttribute("finalPrice", cartService.calculateTotalPrice() - cartService.getDiscount());
        model.addAttribute("user", user);

        return "order/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(
            @RequestParam("customerName") String customerName,
            @RequestParam("phoneCustomer") String phoneCustomer,
            @RequestParam("addressCustomer") String addressCustomer,
            @RequestParam("emailCustomer") String emailCustomer,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("descriptionOrder") String descriptionOrder,
            HttpServletRequest request,
            Model model) {

        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";  // Redirect if cart is empty
        }

        if ("momo".equals(paymentMethod)) {
            try {
                String payUrl = moMoPaymentService.createPayment(customerName, phoneCustomer, addressCustomer, emailCustomer, "Payment for order");
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
    @GetMapping("/list")
    public String listOrders(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Order> orders = orderService.getOrdersByUsername(username);

        model.addAttribute("orders", orders);

        return "order/order-list"; // Assuming you have an order-list.html template
    }
    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}

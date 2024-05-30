package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Customers;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;



//    @PostMapping("/submit")
//    public String submitOrder(String customerName) {
//        List<CartItem> cartItems = cartService.getCartItems();
//        if (cartItems.isEmpty()) {
//            return "redirect:/cart"; // Redirect if cart is empty
//        }
//        orderService.createOrder(customerName, cartItems);
//        return "redirect:/order/confirmation";
//    }

    @PostMapping("/submit")
    public String submitOrder(
            @RequestParam("customerName") String customerName,
            @RequestParam("phonecustomer") String phoneCustomer,
            @RequestParam("addresscustomer") String addressCustomer,
            @RequestParam("emailcustomer") String emailCustomer,
            @RequestParam("descriptionorder") String descriptionOrder) {

        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";  // Redirect if cart is empty
        }

        orderService.createOrder(customerName, phoneCustomer, addressCustomer, emailCustomer, descriptionOrder, cartItems);
        return "redirect:/order/confirmation";
    }


    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}

package com.example.demo.controller;

import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void addAttributes(Model model, HttpSession session) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.calculateTotalPrice());
        model.addAttribute("discount", cartService.getDiscount());
        model.addAttribute("finalPrice", cartService.calculateTotalPrice() - cartService.getDiscount());

        // Add success or error message to the model
        Object discountMessage = session.getAttribute("discountMessage");
        if (discountMessage != null) {
            model.addAttribute("discountMessage", discountMessage);
            session.removeAttribute("discountMessage"); // Remove the message after displaying it
        }
    }

    @GetMapping
    public String showCart(Model model) {
        return "cart/cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);
        return ResponseEntity.ok().body("{\"message\": \"Sản phẩm được thêm vào giỏ hàng thành công!!\"}");
    }


    @PostMapping("/applyDiscount")
    public String applyDiscountCode(@RequestParam String discountCode, HttpSession session) {
        boolean isValid = cartService.applyDiscountCode(discountCode);
        if (isValid) {
            session.setAttribute("discountMessage", "Áp dụng mã giảm giá thành công!");
        } else {
            session.setAttribute("discountMessage", "Mã giảm giá không hợp lệ hoặc hết hạn!");
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}

package com.example.demo.service;

import com.example.demo.model.CartItem;
import com.example.demo.model.DiscountCode;
import com.example.demo.model.Product;
import com.example.demo.repository.DiscountCodeRepository;
import com.example.demo.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();
    private double discount = 0.0;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        CartItem existingCartItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        } else {
            cartItems.add(new CartItem(product, quantity));
        }
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clearCart() {
        cartItems.clear();
        discount = 0;
    }

    public double calculateTotalPrice() {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    public double getDiscount() {
        return discount;
    }

    public boolean applyDiscountCode(String code) {
        Optional<DiscountCode> optionalDiscountCode = discountCodeRepository.findByCode(code);

        if (optionalDiscountCode.isPresent()) {
            DiscountCode discountCode = optionalDiscountCode.get();

            if (isValidDiscountCode(discountCode)) {
                if (discountCode.isPercentage()) {
                    discount = calculateTotalPrice() * (discountCode.getDiscountAmount() / 100);
                } else {
                    discount = discountCode.getDiscountAmount();
                }
                discountCode.setUsedCount(discountCode.getUsedCount() + 1);
                discountCodeRepository.save(discountCode);
                return true;
            }
        }
        return false;
    }

    private boolean isValidDiscountCode(DiscountCode discountCode) {
        LocalDateTime now = LocalDateTime.now();
        boolean withinValidityPeriod = (discountCode.getValidFrom() == null || now.isAfter(discountCode.getValidFrom())) &&
                (discountCode.getValidUntil() == null || now.isBefore(discountCode.getValidUntil()));
        boolean withinUsageLimit = discountCode.getUsageLimit() == 0 || discountCode.getUsedCount() < discountCode.getUsageLimit();
        return withinValidityPeriod && withinUsageLimit;
    }
}

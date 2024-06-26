package com.example.demo.service;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private IProductRepository productRepository;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Check if the product is already in the cart
        CartItem existingCartItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            // If the product is already in the cart, update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        } else {
            // If the product is not in the cart, add a new CartItem
            cartItems.add(new CartItem(product, quantity));
        }
    }
    public int getCartItemCount() {
        List<CartItem> cartItems = getCartItems(); // Assume getCartItems() returns a list of cart items
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
    }

    public double calculateTotalPrice() {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}

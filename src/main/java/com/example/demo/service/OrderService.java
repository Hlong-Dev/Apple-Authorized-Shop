package com.example.demo.service;

import com.example.demo.model.CartItem;
import com.example.demo.model.Customers;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.repository.ICustomerRepository;
import com.example.demo.repository.IOrderDetailRepository;
import com.example.demo.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IOrderDetailRepository orderDetailRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ICustomerRepository customerRepository;

    public void createOrder(String customerName, String phoneCustomer, String addressCustomer, String emailCustomer, String descriptionOrder, List<CartItem> cartItems) {
        // Create and save customer entity
        Customers customer = new Customers();
        customer.setNameCustomer(customerName);
        customer.setPhoneCustomer(phoneCustomer);
        customer.setAddressCustomer(addressCustomer);
        customer.setEmailCustomer(emailCustomer);
        customerRepository.save(customer);

        // Create and save order entity
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setDescriptionOrder(descriptionOrder);
        order.setCustomers(customer); // Set the customer for the order

        double totalOrderPrice = 0.0; // Initialize total order price

        // Save order details for each cart item
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());

            // Calculate total price for this order detail
            double totalPrice = item.getQuantity() * item.getProduct().getPrice();
            detail.setTotalPrice(totalPrice);

            totalOrderPrice += totalPrice; // Add to total order price

            orderDetailRepository.save(detail);
        }

        order.setTotalOrderPrice(totalOrderPrice); // Set total order price to order entity

        orderRepository.save(order);
    }

    public long getOrderCount() {
        return orderRepository.count();
    }

    public double getTotalRevenue() {
        double totalRevenue = 0.0;
        List<Order> orders = orderRepository.findAll(); // Fetch all orders

        for (Order order : orders) {
            List<OrderDetail> orderDetails = order.getOrderDetails();
            for (OrderDetail detail : orderDetails) {
                totalRevenue += detail.getTotalPrice(); // Assuming getTotalPrice() returns the total price for this order detail
            }
        }

        return totalRevenue;
    }

    @Transactional
    public void saveOrderAfterPayment(Order order, List<OrderDetail> orderDetails) {
        orderRepository.save(order);
        for (OrderDetail detail : orderDetails) {
            orderDetailRepository.save(detail);
        }
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByCustomerName(username);
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    // Other methods as needed...

}
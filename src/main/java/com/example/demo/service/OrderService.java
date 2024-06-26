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

    @Transactional
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
        orderRepository.save(order);

        // Save order details for each cart item
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }
    }
}

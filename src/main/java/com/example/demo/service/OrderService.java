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
    private CartService cartService; // Assuming you have a CartService

    @Autowired
    private ICustomerRepository customerRepository;
//    @Transactional
//    public Order createOrder(String customerName, List<CartItem> cartItems) {
//        Order order = new Order();
//        order.setCustomerName(customerName);
//        order = orderRepository.save(order);
//        for (CartItem item : cartItems) {
//            OrderDetail detail = new OrderDetail();
//            detail.setOrder(order);
//            detail.setProduct(item.getProduct());
//            detail.setQuantity(item.getQuantity());
//            orderDetailRepository.save(detail);
//        }
//// Optionally clear the cart after order placement
//        cartService.clearCart();
//        return order;
//    }


    @Transactional
    public void createOrder(String customerName, String phoneCustomer, String addressCustomer, String emailCustomer, String descriptionOrder, List<CartItem> cartItems) {
        // Tạo và thiết lập các thông tin khách hàng, có thể thông qua đối tượng Customer hoặc trực tiếp nếu bạn quản lý như vậy
        Customers customer = new Customers();
        customer.setNameCustomer(customerName);
        customer.setPhoneCustomer(phoneCustomer);
        customer.setAddressCustomer(addressCustomer);
        customer.setEmailCustomer(emailCustomer);
        customerRepository.save(customer);  // Lưu thực thể customer

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setDescriptionOrder(descriptionOrder);


        // Lưu đơn hàng và xử lý các mục trong giỏ hàng
        orderRepository.save(order);
        // Bổ sung thêm logic xử lý các CartItem tương ứng
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }
        // Optionally clear the cart after order placement
        cartService.clearCart();

    }

}

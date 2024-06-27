package com.example.demo.controller;


import com.example.demo.config.Config;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.demo.config.Config.vnp_Command;
import static com.example.demo.config.Config.vnp_Version;
@Controller
@RequestMapping("/api/payment")
public class PaymentVnpayController {

    private final CartService cartService;
    private final OrderService orderService;

    @Autowired
    public PaymentVnpayController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/create_payment")
    public RedirectView createPayment(HttpServletRequest req) throws UnsupportedEncodingException {
        String orderType = "other";
        double totalPrice = cartService.calculateTotalPrice();
        double discount = cartService.getDiscount();
        double finalPrice = totalPrice - discount;
        String amount = String.valueOf((int) (finalPrice * 100)); // Convert final price to integer string
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = Config.getIpAddress(req);
        String vnp_TmnCode = Config.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", Config.vnp_Version);
        vnp_Params.put("vnp_Command", Config.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        cartService.clearCart();
        return new RedirectView(paymentUrl);
    }

    @GetMapping("/vnpay_return")
    public ModelAndView vnpayReturn(
            @RequestParam(value = "vnp_Amount", required = false) String amount,
            @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
            @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
            @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode) {

        ModelAndView modelAndView = new ModelAndView();
        if ("00".equals(responseCode)) {
            // Payment successful, save order to database
            Order order = new Order();
            order.setCustomerName("Customer Name"); // Set the actual customer name
            order.setDescriptionOrder(orderInfo); // Set the actual order description
            List<CartItem> cartItems = cartService.getCartItems(); // Get cart items
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (CartItem item : cartItems) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProduct(item.getProduct());
                detail.setQuantity(item.getQuantity());
                orderDetails.add(detail);
            }
            orderService.saveOrderAfterPayment(order, orderDetails);

            modelAndView.setViewName("success");
            modelAndView.addObject("message", "Payment successful!");
        } else {
            modelAndView.setViewName("failure");
            modelAndView.addObject("message", "Payment failed. Please try again.");
        }
        return modelAndView;
    }
}

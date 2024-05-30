package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "descriptionorder")
    private String descriptionOrder;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;



    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "customers_id")
    private Customers customers;

    @ManyToOne
    @JoinColumn(name = "shippers_id")
    private Shippers shippers;

    // Constructors, Getters and Setters
}

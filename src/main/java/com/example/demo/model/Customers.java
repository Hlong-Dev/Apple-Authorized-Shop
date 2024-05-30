package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "namecustomer")
    private String nameCustomer;

    @Column(name = "phonecustomer")
    private String phoneCustomer;

    @Column(name = "addresscustomer")
    private String addressCustomer;

    @Column(name = "emailcustomer")
    private String emailCustomer;
}

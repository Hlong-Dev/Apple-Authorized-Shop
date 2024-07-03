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
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "nameproduct")
    private String nameProduct;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "statusproduct")
    private boolean statusProduct;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "suppliers_id")
    private Suppliers suppliers;

    private String imagePath;

    // New field for sale price
    @Column(name = "saleprice")
    private Double salePrice;

    // Constructors, Getters and Setters
}

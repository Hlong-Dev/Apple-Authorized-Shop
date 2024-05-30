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
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metatitle")
    private String metaTitle;

    @Column(name = "nameproductcategory")
    private String nameProductCategory;

    @Column(name = "statusproductcategory")
    private boolean statusProductCategory;

    // Constructors, Getters and Setters

}

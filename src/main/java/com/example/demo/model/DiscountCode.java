package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "DiscountCode")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private double discountAmount;
    private boolean isPercentage;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private int usageLimit;
    private int usedCount;
}

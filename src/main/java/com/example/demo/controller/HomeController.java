package com.example.demo.controller;

import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("message", "Trang web bán hàng");
        model.addAttribute("products", productService.getAllProducts());
        return "home/home";
    }
}

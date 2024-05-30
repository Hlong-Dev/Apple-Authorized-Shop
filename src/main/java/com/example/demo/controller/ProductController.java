package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@RequestMapping("/products")
public class ProductController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }

        if (!file.isEmpty()) {
            try {
                String filename = file.getOriginalFilename();
                Path path = Paths.get(uploadPath + filename);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                product.setImagePath("/uploads/" + filename);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Failed to upload image: " + e.getMessage());
                return "redirect:/products/add";
            }
        }

        productService.addProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }

        if (!file.isEmpty()) {
            try {
                String filename = file.getOriginalFilename();
                Path path = Paths.get(uploadPath + filename);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                product.setImagePath("/uploads/" + filename);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Failed to upload image: " + e.getMessage());
                return "redirect:/products/update/" + id;
            }
        }

        productService.updateProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}

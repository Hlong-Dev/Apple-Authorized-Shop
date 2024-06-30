package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.*;

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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class AdminController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/home")
    public String adminHome(Model model) {
        long productCount = productService.getProductCount();
        long orderCount = orderService.getOrderCount();
        long userCount = userService.getUserCount();
        double totalRevenue = orderService.getTotalRevenue();
        long accessLogCount = accessLogService.getAccessLogCount(); // Get access log count
        List<Object[]> accessLogStats = accessLogService.getAccessLogStats(); // Get access log stats

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("productCount", productCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("accessLogCount", accessLogCount); // Add access log count to model
        model.addAttribute("accessLogStats", accessLogStats); // Add access log stats to model

        return "/admin/home"; // Điều hướng đến trang home của admin
    }
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/admin/product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/admin/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/admin/add-product";
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
                return "redirect:/admin/products/add";
            }
        }

        productService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/admin/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/admin/update-product";
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
                return "redirect:/admin/products/update/" + id;
            }
        }

        productService.updateProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/view/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "/admin/view-product";
        } else {
            throw new IllegalArgumentException("Invalid product Id:" + id);
        }
    }
}

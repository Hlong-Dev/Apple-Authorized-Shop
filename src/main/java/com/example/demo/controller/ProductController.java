package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/category/{categoryId}")
    public String showProductsByCategory(@PathVariable Long categoryId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "4") int size,
                                         Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getProductsByCategory(categoryId, pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);

        return "/products/product-list";
    }
    @GetMapping
    public String showProductList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getAllProducts(pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/product-list";
    }
    @GetMapping("/all")
    public String showAllProducts(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "4") int size,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getAllProducts(pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getAllCategories());

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

    // New method to view product details
    @GetMapping("/view/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "/products/view-product";
        } else {
            throw new IllegalArgumentException("Invalid product Id:" + id);
        }
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "4") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.searchProducts(keyword, pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "/products/product-list";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<Map<String, String>> autocompleteProducts(@RequestParam("term") String keyword) {
        List<Product> productList = productService.searchProducts(keyword);
        return productList.stream()
                .map(product -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("id", String.valueOf(product.getId()));
                    result.put("name", product.getNameProduct());
                    result.put("imagePath", product.getImagePath());
                    return result;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/sale")
    public String showSaleProducts(Model model) {
        List<Product> saleProducts = productService.getAllProducts().stream()
                .filter(product -> product.getSalePrice() != null)
                .collect(Collectors.toList());

        model.addAttribute("saleProducts", saleProducts);
        return "/products/sale-products";
    }
}
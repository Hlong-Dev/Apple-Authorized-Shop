package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final IProductRepository productRepository;
    private final AccessLogService accessLogService; // Inject AccessLogService

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        accessLogService.logAccess("getAllProducts"); // Log access
        return products;
    }
    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    // Add a new product to the database
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
    // Update an existing product
    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingProduct.setNameProduct(product.getNameProduct());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());

//        existingProduct.setStatusProduct(product.getStatusProduct());

        existingProduct.setManufacturer(product.getManufacturer());
        existingProduct.setMetaTitle(product.getMetaTitle());
        existingProduct.setImage(product.getImage());
        existingProduct.setMoreImage(product.getMoreImage());

        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        return productRepository.save(existingProduct);
    }
    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }
    public long getProductCount() {
        return productRepository.count(); // Assuming you have a JpaRepository
    }
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameProductContainingIgnoreCase(keyword);
    }
}

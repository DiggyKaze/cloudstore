package com.caniz.services;

import com.caniz.models.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadProductsOnStartup() {
        try {
            refreshProducts();
        } catch (Exception e) {
            System.out.println("Could not load products on startup: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 300000)
    public void refreshProducts() {
        try {
            Product[] fetchedProducts = restTemplate.getForObject(
                    "https://fakestoreapi.com/products",
                    Product[].class
            );

            if (fetchedProducts != null) {
                products.clear();
                for (Product product : fetchedProducts) {
                    products.put(product.getId(), product);
                }
                System.out.println("Products updated: " + products.size());
            }
        } catch (Exception e) {
            System.out.println("Failed to refresh products: " + e.getMessage());
        }
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public Product getProductById(Long id) {
        return products.get(id);
    }
}

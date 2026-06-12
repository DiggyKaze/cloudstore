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
        refreshProducts();
    }

    @Scheduled(fixedRate = 300000)
    public void refreshProducts() {
        Product[] fetchedProducts = restTemplate.getForObject(
                "https://fakestoreapi.com/products",
                Product[].class
        );

        products.clear();

        if (fetchedProducts != null) {
            for (Product product : fetchedProducts) {
                products.put(product.getId(), product);
            }
        }

        System.out.println("Products updated: " + products.size());
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public Product getProductById(Long id) {
        return products.get(id);
    }
}

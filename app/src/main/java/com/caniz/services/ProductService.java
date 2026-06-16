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

    

    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate = createRestTemplateWithUserAgent();

    private static RestTemplate createRestTemplateWithUserAgent() {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            return execution.execute(request, body);
        });
        return template;
    }



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

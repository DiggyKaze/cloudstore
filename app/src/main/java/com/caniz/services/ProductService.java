package com.caniz.services;

import com.caniz.models.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class ProductService {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = createRestTemplateWithUserAgent();

    @PostConstruct
    public void loadProductsOnStartup() {
        refreshProducts();
    }

    @Scheduled(fixedRate = 300000)
    public void refreshProducts() {
        try {
            Product[] fetchedProducts = restTemplate.getForObject(
                    "https://fakestoreapi.com/products",
                    Product[].class
            );

            if (fetchedProducts != null && fetchedProducts.length > 0) {
                products.clear();

                for (Product product : fetchedProducts) {
                    products.put(product.getId(), product);
                }

                System.out.println("Products updated from FakeStore API: " + products.size());
                return;
            }

            loadFallbackProducts();

        } catch (Exception e) {
            System.out.println("Failed to refresh products from FakeStore API: " + e.getMessage());
            loadFallbackProducts();
        }
    }

    private void loadFallbackProducts() {
        if (!products.isEmpty()) {
            return;
        }

        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("Fallback Product 1");
        p1.setPrice(199.0);
        p1.setDescription("Product loaded locally because FakeStore API was unavailable.");
        p1.setCategory("fallback");
        p1.setImage("https://via.placeholder.com/150");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("Fallback Product 2");
        p2.setPrice(299.0);
        p2.setDescription("Product loaded locally because FakeStore API was unavailable.");
        p2.setCategory("fallback");
        p2.setImage("https://via.placeholder.com/150");

        products.put(p1.getId(), p1);
        products.put(p2.getId(), p2);

        System.out.println("Fallback products loaded: " + products.size());
    }

    private static RestTemplate createRestTemplateWithUserAgent() {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "Mozilla/5.0");
            return execution.execute(request, body);
        });
        return template;
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public Product getProductById(Long id) {
        return products.get(id);
    }
}
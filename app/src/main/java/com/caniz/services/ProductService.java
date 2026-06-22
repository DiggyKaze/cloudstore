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
            request.getHeaders().set(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            );
            request.getHeaders().set("Accept", "application/json");
            return execution.execute(request, body);
        });

        return template;
    }

    @PostConstruct
    public void loadProductsOnStartup() {
        refreshProducts();
    }

    @Scheduled(fixedDelay = 300000)
    public void refreshProducts() {
        System.out.println("Scheduled product refresh started");

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

            System.out.println("FakeStore API returned no products");
            loadFallbackProducts();

        } catch (Exception e) {
            System.out.println("Failed to refresh products from FakeStore API: " + e.getMessage());
            loadFallbackProducts();
        }
    }

    private void loadFallbackProducts() {
        if (!products.isEmpty()) {
            System.out.println("Keeping existing products: " + products.size());
            return;
        }

        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("Fjällräven Backpack");
        p1.setPrice(109.95);
        p1.setDescription("Fallback product shown when FakeStore API is unavailable.");
        p1.setCategory("men's clothing");
        p1.setImage("https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("Mens Casual Premium Slim Fit T-Shirt");
        p2.setPrice(22.30);
        p2.setDescription("Fallback product shown when FakeStore API is unavailable.");
        p2.setCategory("men's clothing");
        p2.setImage("https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg");

        Product p3 = new Product();
        p3.setId(3L);
        p3.setTitle("Samsung 49-Inch Monitor");
        p3.setPrice(999.99);
        p3.setDescription("Fallback product shown when FakeStore API is unavailable.");
        p3.setCategory("electronics");
        p3.setImage("https://fakestoreapi.com/img/81Zt42ioCgL._AC_SX679_.jpg");

        products.put(p1.getId(), p1);
        products.put(p2.getId(), p2);
        products.put(p3.getId(), p3);

        System.out.println("Fallback products loaded: " + products.size());
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public Product getProductById(Long id) {
        return products.get(id);
    }
}


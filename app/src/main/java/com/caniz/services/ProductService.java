package com.caniz.services;

import com.caniz.models.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final WebClient webClient;

    public ProductService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://fakestoreapi.com")
                .defaultHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                )
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @PostConstruct
    public void loadProductsOnStartup() {
        refreshProducts();
    }

    @Scheduled(fixedDelay = 300000)
    public void refreshProducts() {
        System.out.println("Scheduled product refresh started");

        try {
            Product[] fetchedProducts = webClient.get()
                    .uri("/products")
                    .retrieve()
                    .bodyToMono(Product[].class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            products.clear();

            if (fetchedProducts != null && fetchedProducts.length > 0) {
                for (Product product : fetchedProducts) {
                    products.put(product.getId(), product);
                }

                System.out.println("Products updated from FakeStore API: " + products.size());
            } else {
                System.out.println("FakeStore API returned no products");
            }

        } catch (WebClientResponseException e) {
            System.out.println("FakeStore API HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            products.clear();

        } catch (Exception e) {
            System.out.println("Failed to refresh products from FakeStore API: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            products.clear();
        }
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public Product getProductById(Long id) {
        return products.get(id);
    }
}


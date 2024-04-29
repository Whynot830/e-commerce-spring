package org.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.ecommerce.dto.ProductDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductPage extends BasicResponse {
    @JsonProperty
    private final List<ProductDTO> products;

    @JsonProperty
    int totalPages;

    @JsonProperty
    int currentPage;

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getId(), product.getTitle(), product.getPrice(),
                product.getImgName(), product.getCategory().getName());
    }

    public ProductPage(int statusCode, Page<Product> productPage) {
        super(statusCode);
        this.products = productPage.map(this::toDTO).toList();
        this.totalPages = productPage.getTotalPages();
        this.currentPage = productPage.getPageable().getPageNumber();
    }

    public ProductPage(int statusCode, List<Product> products) {
        super(statusCode);
        this.products = products.stream().map(this::toDTO).toList();
        this.totalPages = 1;
        this.currentPage = 0;
    }

    public static ProductPage ok(Page<Product> productPage) {
        return new ProductPage(200, productPage);
    }

    public static ProductPage ok(List<Product> products) {
        return new ProductPage(200, products);
    }
}

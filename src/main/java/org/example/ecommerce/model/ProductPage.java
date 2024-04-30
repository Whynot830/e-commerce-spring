package org.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.ProductDTO;
import org.example.ecommerce.mapper.ProductDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductPage {
    private final ProductDTOMapper mapper;

    @JsonProperty
    private final List<ProductDTO> products;

    @JsonProperty
    int totalPages;

    @JsonProperty
    int currentPage;

    public ProductPage(ProductDTOMapper mapper, Page<Product> productPage) {
        this.mapper = mapper;
        this.products = productPage.map(mapper).toList();
        this.totalPages = productPage.getTotalPages();
        this.currentPage = productPage.getPageable().getPageNumber();
    }

    public static ProductPage ok(ProductDTOMapper mapper, Page<Product> productPage) {
        return new ProductPage(mapper, productPage);
    }
}

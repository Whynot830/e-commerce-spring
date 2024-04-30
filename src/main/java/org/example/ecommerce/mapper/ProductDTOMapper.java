package org.example.ecommerce.mapper;

import org.example.ecommerce.dto.ProductDTO;
import org.example.ecommerce.model.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProductDTOMapper implements Function<Product, ProductDTO> {
    @Override
    public ProductDTO apply(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getImgName(),
                product.getCategory().getName()
        );
    }
}

package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.ProductDTO;
import org.example.ecommerce.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Products API")
public class ProductController {
    private final ProductService productService;

//    @PostMapping()
//    public ResponseEntity<ProductDTO> create(
//            @RequestPart ProductDTO productDTO,
//            @RequestPart(required = false) MultipartFile file
//    ) {
//        var savedProduct = productService.create(productDTO, file);
//        return ResponseEntity.created(null).body(savedProduct);
//    }

    @PostMapping()
    public ResponseEntity<ProductDTO> create(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam String category,
            @RequestParam(required = false) MultipartFile file
    ) {
        var savedProduct = productService.create(new ProductDTO(null, title, description, price, null, category), file);
        return ResponseEntity.created(null).body(savedProduct);
    }

    @PostMapping(params = "multiple")
    public ResponseEntity<List<ProductDTO>> create(
            @RequestBody List<ProductDTO> products,
            @RequestParam(name = "multiple") String ignored
    ) {
        var savedProducts = productService.create(products);
        return ResponseEntity.created(null).body(savedProducts);
    }

    @GetMapping
    public ResponseEntity<?> read(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer limit,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String order
    ) {
        var response = productService.read(category, page, limit, sort, order);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> read(@PathVariable Long id) {
        var product = productService.read(id);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @RequestPart(required = false) ProductDTO newProduct,
            @RequestPart(required = false) MultipartFile file
    ) {
        var updatedProduct = productService.update(id, newProduct, file);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteAll() {
        productService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}

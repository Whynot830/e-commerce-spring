package org.example.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.ProductDTO;
import org.example.ecommerce.mapper.ProductDTOMapper;
import org.example.ecommerce.model.Product;
import org.example.ecommerce.model.ProductPage;
import org.example.ecommerce.repository.CategoryRepository;
import org.example.ecommerce.repository.OrderItemRepository;
import org.example.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.ecommerce.util.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final CategoryService categoryService;
    private final OrderItemRepository orderItemRepo;
    private final ImageService imageService;
    private final ProductDTOMapper mapper;

    private Product save(ProductDTO productDTO, MultipartFile file) {
        if (isNullOrBlank(productDTO.title()) || isNullOrBlank(productDTO.category())
                || productDTO.price() == null || isNullOrBlank(productDTO.description()))
            throw new IllegalArgumentException("Some properties are null or blank");

        if (productDTO.price().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be a non-negative value");

        var category = categoryService
                .getCategory(productDTO.category())
                .orElseThrow(() -> new IllegalArgumentException("Category with given name not found"));

        var product = Product.builder()
                .title(productDTO.title())
                .description(productDTO.description())
                .price(productDTO.price())
                .category(category)
                .build();

        if (file != null) {
            imageService.create(file);
            product.setImgName(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'));
        }
        return productRepo.save(product);
    }

    public ProductDTO create(ProductDTO productDTO, MultipartFile file) {
        var product = save(productDTO, file);
        return mapper.apply(product);
    }

    @Transactional
    public List<ProductDTO> create(List<ProductDTO> products) {
        List<Product> savedProducts = new ArrayList<>();
        Product savedProduct;
        for (var product : products) {
            savedProduct = save(product, null);
            savedProducts.add(savedProduct);
        }
        return savedProducts.stream().map(mapper).toList();
    }

    public ProductPage read(String categoryName, Integer page, String sort, String order) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.fromString(order), sort);
        if (categoryName != null) {
            var category = categoryService
                    .getCategory(categoryName)
                    .orElseThrow(() -> new IllegalArgumentException("Category with given name not found"));
            var products = productRepo.findByCategory(category, pageable);
            return ProductPage.ok(mapper, products);
        }
        var products = productRepo.findAll(pageable);
        return ProductPage.ok(mapper, products);
    }

    public ProductDTO read(Long id) {
        return productRepo.findById(id)
                .map(mapper)
                .orElseThrow(EntityNotFoundException::new);
    }

    public ProductDTO update(Long id, ProductDTO newProduct, MultipartFile file) {
        var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);

        if (newProduct != null) {
            var category = categoryRepo.findByName(newProduct.category()).orElse(null);
            if (newProduct.category() != null && category == null)
                throw new IllegalArgumentException("Category with given name not found");
            if (newProduct.price() != null && newProduct.price().compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Price must be a non-negative value");

            if (!isNullOrBlank(newProduct.title())) product.setTitle(newProduct.title());
            if (!isNullOrBlank(newProduct.description())) product.setDescription(newProduct.description());
            if (newProduct.price() != null) product.setPrice(newProduct.price());
            if (!isNullOrBlank(newProduct.category())) product.setCategory(category);
        }

        if (file != null) {
            var presentImage = imageService.read(product.getImgName());

            if (presentImage == null) imageService.create(file);
            else imageService.update(file, presentImage.name());

            product.setImgName(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'));
        }
        var items = product.getOrderItems();
        orderItemRepo.saveAll(items);
        product = productRepo.save(product);

        return mapper.apply(product);
    }

    public void delete(Long id) {
        var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        productRepo.delete(product);
    }

    public void deleteAll() {
        productRepo.deleteAll();
    }
}

package org.example.ecommerce.util;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.model.Category;
import org.example.ecommerce.dto.ProductDTO;
import org.example.ecommerce.dto.UserDTO;
import org.example.ecommerce.service.CategoryService;
import org.example.ecommerce.service.ProductService;
import org.example.ecommerce.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupInitializer implements CommandLineRunner {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Override
    public void run(String... args) {
        try {
            userService.create(new UserDTO("admin", "adm1n.e.commerce@mail.ru",
                    "admin", "ADMIN"));
        } catch (DataIntegrityViolationException ignored) {
        }
        try {
            categoryService.create(Category.builder().name("кровати").build());
            categoryService.create(Category.builder().name("стулья").build());
            categoryService.create(Category.builder().name("столы").build());
        } catch (DataIntegrityViolationException ignored) {
        }
        try {

            productService.create(List.of(
//                    new ProductDTO(null, "Wooden Bed with Striped Bedding", "A rustic bed frame crafted from distressed wood with a cozy striped bedding set", BigDecimal.valueOf(799.99), "bed-wood.jpg", "beds"),
                    new ProductDTO(null, "Деревянная кровать с полосатым постельным бельем", "Кровать в деревенском стиле с каркасом из потертого дерева и уютным комплектом постельного белья в полоску", BigDecimal.valueOf(799.99), "bed-wood.jpg", "кровати"),
//                    new ProductDTO(null, "Upholstered Bed with Cushioned Headboard", "A plush and stylish bed with a tufted, cushioned headboard in soft beige upholstery", BigDecimal.valueOf(1199.99), "bed-beige.webp", "beds"),
                    new ProductDTO(null, "Мягкая кровать с мягким изголовьем", "Роскошная и стильная кровать с мягким изголовьем в мягкой бежевой обивке", BigDecimal.valueOf(1199.99), "bed-beige.webp", "кровати"),
//                    new ProductDTO(null, "Contemporary Platform Bed", "A sleek and modern platform bed with a low-profile design in a rich brown upholstery", BigDecimal.valueOf(999.99), "bed-beige-2.webp", "beds"),
                    new ProductDTO(null, "Современная кровать на платформе", "Изящная и современная кровать-платформа с низкопрофильным дизайном и богатой коричневой обивкой", BigDecimal.valueOf(999.99), "bed-beige-2.webp", "кровати"),
//                    new ProductDTO(null, "Tufted Bed with Wingback Headboard", "A luxurious bed with a tall, wingback-style headboard featuring deep button tufting in a rich navy blue velvet", BigDecimal.valueOf(1499.99), "bed-blue.jpg", "beds"),
                    new ProductDTO(null, "Кровать с тафтингом и откидным изголовьем", "Роскошная кровать с высоким изголовьем в виде откидной спинки и глубоким тафтингом на пуговицах из насыщенного темно-синего бархата", BigDecimal.valueOf(1499.99), "bed-blue.jpg", "кровати"),
//                    new ProductDTO(null, "Minimalist Bed with Upholstered Frame", "A streamlined and minimalist bed with a simple upholstered frame in a soft gray hue", BigDecimal.valueOf(899.99), "bed-brown.jpg", "beds")
            new ProductDTO(null, "Минималистская кровать с мягким каркасом", "Обтекаемая и минималистичная кровать с простым каркасом из мягкой обивки мягкого серого оттенка", BigDecimal.valueOf(899.99), "bed-brown.jpg", "кровати")

                    ));
        } catch (DataIntegrityViolationException ignored) {
        }
    }
}

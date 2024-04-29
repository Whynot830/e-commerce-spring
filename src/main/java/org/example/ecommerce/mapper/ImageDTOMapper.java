package org.example.ecommerce.mapper;

import org.example.ecommerce.dto.ImageDTO;
import org.example.ecommerce.model.Image;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ImageDTOMapper implements Function<Image, ImageDTO> {
    @Override
    public ImageDTO apply(Image image) {
        return new ImageDTO(
                image.getId(),
                image.getName(),
                image.getType()
        );
    }
}

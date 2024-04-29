package org.example.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.mapper.ImageDTOMapper;
import org.example.ecommerce.model.Image;
import org.example.ecommerce.dto.ImageDTO;
import org.example.ecommerce.repository.ImageRepository;
import org.example.ecommerce.util.ImageUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepo;
    private final ImageDTOMapper mapper;

    private ImageDTO save(MultipartFile file) {
        try {
            var savedImage = imageRepo.save(Image.builder()
                    .name(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'))
                    .type(file.getContentType())
                    .imageData(ImageUtils.compressImage(file.getBytes()))
                    .build());
            return mapper.apply(savedImage);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public ImageDTO create(MultipartFile file) {
        return save(file);
    }

    @Transactional
    public List<ImageDTO> create(MultipartFile[] files) {
        List<ImageDTO> savedImages = new ArrayList<>();
        ImageDTO savedImage;
        for (var file : files) {
            savedImage = save(file);
            savedImages.add(savedImage);
        }
        return savedImages;
    }

    public List<ImageDTO> readAll() {
        return imageRepo.findAll().stream().map(mapper).toList();
    }

    public ImageDTO read(String filename) {
        return imageRepo.findByName(filename).map(mapper).orElse(null);
    }

    public byte[] getImage(String filename) {
        try {
            var imageData = imageRepo.findByName(filename).orElseThrow(EntityNotFoundException::new);
            return ImageUtils.decompressImage(imageData.getImageData());
        } catch (DataFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImageDTO update(MultipartFile file, String filename) {
        var oldImage = imageRepo.findByName(filename).orElseThrow(EntityNotFoundException::new);
        try {
            var updatedImage = imageRepo.save(Image.builder()
                    .id(oldImage.getId())
                    .name(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'))
                    .type(file.getContentType())
                    .imageData(ImageUtils.compressImage(file.getBytes()))
                    .build());
            return mapper.apply(updatedImage);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void delete(String name) {
        var image = imageRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
        imageRepo.delete(image);
    }

    public void deleteAll() {
        imageRepo.deleteAll();
    }
}

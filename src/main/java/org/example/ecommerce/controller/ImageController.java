package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.ImageDTO;
import org.example.ecommerce.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "Images", description = "Images API")
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageDTO> create(@RequestParam MultipartFile file) {
        var savedImage = imageService.create(file);
        return ResponseEntity.created(null).body(savedImage);
    }

    @PostMapping(params = "multiple")
    public ResponseEntity<List<ImageDTO>> create(
            @RequestParam MultipartFile[] files,
            @RequestParam(name = "multiple") String ignored
    ) {
        var savedImages = imageService.create(files);
        return ResponseEntity.created(null).body(savedImages);
    }

    @GetMapping
    public ResponseEntity<List<ImageDTO>> readAll() {
        var images = imageService.readAll();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> getImage(@PathVariable String name) {
        byte[] imageData = imageService.getImage(name);
        return ResponseEntity.status(OK).contentType(IMAGE_PNG).body(imageData);
    }

    @PatchMapping("/{name}")
    public ResponseEntity<ImageDTO> update(
            @RequestParam MultipartFile file,
            @PathVariable String name
    ) {
        var updatedImage = imageService.update(file, name);
        return ResponseEntity.ok(updatedImage);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        imageService.delete(name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        imageService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}

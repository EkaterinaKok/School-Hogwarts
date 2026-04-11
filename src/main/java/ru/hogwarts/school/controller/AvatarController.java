package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCover(@PathVariable Long id, @RequestParam MultipartFile cover) throws IOException {
        if (cover.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }

        if (!cover.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Only image files are allowed");
        }

        avatarService.uploadCover(id, cover);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/cover/preview")
    public ResponseEntity<byte[]> downloadCover(@PathVariable Long id) {
        Avatar avatar = avatarService.findAvatar(id);

        if (avatar.getData() == null || avatar.getData().length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(avatar.getData());
    }

    @GetMapping(value = "/{id}/cover")
    public ResponseEntity<byte[]> downloadOriginalCover(@PathVariable Long id) throws IOException {
        Avatar avatar = avatarService.findAvatar(id);
        Path path = Path.of(avatar.getFilePath());

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileData = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(fileData.length);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fileData);
    }
}



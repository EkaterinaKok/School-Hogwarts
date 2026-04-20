package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {

    @Value("covers")
    private String coversDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadCover(Long studentId, MultipartFile file) throws IOException {
        Student student = studentService.findStudent(studentId);
        Path filePath = Path.of(coversDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateImageData(filePath));
        avatarRepository.save(avatar);
    }

    private byte[] generateImageData(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(bis);
            if (originalImage == null) {
                throw new IOException("Не удалось прочитать изображение: " + filePath);
            }

            // Параметры миниатюры
            int targetWidth = 100;
            int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalImage.getWidth()));

            // Создаём новое RGB‑изображение (без прозрачности)
            BufferedImage preview = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

            // Получаем графический контекст и заливаем белый фон
            Graphics2D g2d = preview.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, targetWidth, targetHeight);

            // Настройки для качественного масштабирования
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Масштабируем и рисуем изображение
            g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            g2d.dispose();

            // Определяем формат (по умолчанию — JPEG)
            String formatName = getExtension(filePath.getFileName().toString().toLowerCase());
            if (!Arrays.asList(ImageIO.getWriterFormatNames()).contains(formatName)) {
                formatName = "jpg";
            }

            // Сохраняем изображение
            ImageIO.write(preview, formatName, baos);
            return baos.toByteArray();
        }
    }


    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "jpg"; // расширение по умолчанию
        }
        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        // Нормализуем расширения
        switch (extension) {
            case "jpeg":
                return "jpg";
            case "tiff":
            case "tif":
                return "tiff";
            default:
                return extension;
        }
    }
}

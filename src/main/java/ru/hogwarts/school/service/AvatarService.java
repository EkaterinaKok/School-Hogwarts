package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {

    @Value("covers")
    private String coversDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadCover(Long studentId, MultipartFile file) throws IOException {
        logger.info("Method uploadCover was invoked for student ID: {}", studentId);
        try {
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

            logger.debug("Successfully uploaded and processed cover for student ID: {}", studentId);
        } catch (EntityNotFoundException e) {
            logger.error("Student not found with ID: {}", studentId, e);
            throw e;
        } catch (IOException e) {
            logger.error("IO error during cover upload for student ID {}: {}", studentId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during cover upload for student ID {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Failed to upload cover", e);
        }
    }

    private byte[] generateImageData(Path filePath) throws IOException {
        logger.info("Method generateImageData was invoked");
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
        logger.info("Method findAvatar was invoked");
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    private String getExtension(String fileName) {
        logger.info("Method getExtension was invoked");
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

    public Page<Avatar> getAllAvatars(int page, int size) {
        logger.info("Method getAllAvatars was invoked");
        Pageable pageable = PageRequest.of(page, size);
        return avatarRepository.findAll(pageable);
    }
}

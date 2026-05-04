package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Method createFaculty was invoked");
        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            logger.error("Failed to create faculty: {}", faculty, e);
            throw e;
        }
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Method editFaculty was invoked");
        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            logger.error("Failed to edit faculty: {}", faculty, e);
            throw e;
        }
    }

    public Faculty findFaculty(Long id) {
        logger.info("Method findFaculty was invoked");
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Faculty not found with id: {}", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Faculty not found with id: " + id
                    );
                });
    }

    public void deleteFaculty(Long id) {
        logger.info("Method deleteFaculty was invoked");
        facultyRepository.deleteById(id);
    }

    public List<Faculty> colorFilter(String color) {
        logger.info("Method colorFilter was invoked");
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public List<Faculty> allFaculties() {
        logger.info("Method allFaculties was invoked");
        return facultyRepository.findAll();
    }

    public List<Faculty> findByNameOfColorFaculties(String name, String color){
        logger.info("Method findByNameOfColorFaculties was invoked");
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(name, color);
    }
}
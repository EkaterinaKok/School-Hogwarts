package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public String getTheLongestFacultyName() {
        Collection<Faculty> faculties = facultyRepository.findAll();

        return faculties.stream()
                .map(Faculty::getName)
                .filter(name -> name != null && !name.isEmpty())
                .max(Comparator.comparingInt(String::length))
                .orElse("No faculties found");
    }

    //Шаг 4.Создать эндпоинт (не важно в каком контроллере), который будет возвращать целочисленное значение.
    public Long findTheStream() {//Calculation findTheStream 73198400 nanosecond
        long startTime = System.nanoTime();

        long sum = Stream
                .iterate(1L, a -> a + 1)
                .limit(1_000_000)
                .reduce(0L, Long::sum);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        logger.info("Calculation findTheStream {} nanosecond", duration);
        return sum;
    }

    //способ уменьшить время ответа эндпоинта путем модификации выражения
    public Long findTheStreamOptimal() {//Calculation findTheFastestStream 50827700 nanoseconds
        logger.info("Start the parallel Stream");
        long startTime = System.nanoTime();

        long sum = IntStream
                .rangeClosed(1, 1_000_000)
                .parallel()
                .asLongStream()
                .sum();

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        logger.info("Calculation findTheFastestStream {} nanoseconds", duration);
        return sum;
    }

    //самый быстрый способ подсчета
    public Long getSum() {//Calculation calculateSum 600 nanoseconds
        logger.info("Was invoked method for calculating the sum of numbers from 1 to 1,000,000");
        int n = 1_000_000;
        return calculateSum(n);
    }

    private Long calculateSum(int n) {
        logger.info("We're timing it");

        long startTime = System.nanoTime();
        long result = (long) n * (n + 1) / 2; // Используем long для вычислений
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        logger.info("Calculation calculateSum {} nanoseconds", duration);

        return result;
    }
}
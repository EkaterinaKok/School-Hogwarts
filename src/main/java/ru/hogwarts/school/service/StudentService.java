package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            logger.error("Failed to create student: {}", student, e);
            throw e;
        }
    }

    public Student editStudent(Student student) {
        logger.info("Method editStudent was invoked");
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            logger.error("Failed to edit student: {}", student, e);
            throw e;
        }
    }

    public Student findStudent(Long id) {
        logger.info("Method findStudent was invoked");
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Student not found with id: {}", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Student not found with id: " + id
                    );
                });
    }

    public void deleteStudent(Long id) {
        logger.info("Method deleteStudent was invoked");
        try {
            studentRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Failed to delete student with id: {}", id, e);
            throw e;
        }
    }

    public List<Student> ageFilter(int age) {
        logger.info("Method ageFilter was invoked");
        try {
            return studentRepository.findByAge(age);
        } catch (Exception e) {
            logger.error("Failed to filter students by age: {}", age, e);
            throw e;
        }
    }

    public List<Student> allStudents() {
        logger.info("Method allStudent was invoked");
        try {
            return studentRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to retrieve all students", e);
            throw e;
        }
    }

    public List<Student> findByAgeBetween(int min, int max) {
        logger.info("Method findByAgeBetween was invoked");
        try {
            return studentRepository.findByAgeBetween(min, max);
        } catch (Exception e) {
            logger.error("Failed to find students by age range: {}-{}", min, max, e);
            throw e;
        }
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        logger.info("Method getFacultyByStudentId was invoked for student ID: {}", studentId);
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> {
                        logger.error("Student not found with ID: {}", studentId);
                        return new EntityNotFoundException("Student not found with id: " + studentId);
                    });

            Faculty faculty = student.getFaculty();
            if (faculty == null) {
                logger.error("Faculty is null for student ID: {}", studentId);
                throw new EntityNotFoundException("Faculty not assigned to student with id: " + studentId);
            }

            logger.debug("Successfully retrieved faculty for student ID: {}", studentId);
            return faculty;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while getting faculty for student ID {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve faculty", e);
        }
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("Method getStudentsByFacultyId was invoked");
        return studentRepository.findByFacultyId(facultyId);
    }

    public long getTotalStudentCount() {
        logger.info("Method getTotalStudentCount was invoked");
        return studentRepository.countAllStudents();
    }

    public double getAverageAge() {
        logger.info("Method getAverageAge was invoked");
        Double averageAge = studentRepository.findAverageAge();
        return averageAge != null ? averageAge : 0.0;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Method getLastFiveStudents was invoked");
        try {
            return studentRepository.findLastFiveStudents();
        } catch (Exception e) {
            logger.error("Failed to retrieve last five students", e);
            throw e;
        }
    }

}
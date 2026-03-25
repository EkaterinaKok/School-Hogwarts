package ru.hogwarts.school.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public Student getStudentInfo(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @GetMapping("ageFilter/{age}")
    public List<Student> ageFilter(@PathVariable int age) {
        return studentService.ageFilter(age);
    }

    @GetMapping
    public List<Student> allStudents() {
        return studentService.allStudents();
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping
    public Student editStudent(@RequestBody Student student) {
        return studentService.editStudent(student);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("filterByAge")
    public List<Student> findByAgeBetweenStudent(@RequestParam("min") int min, @RequestParam("max") int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("/faculty/by-student/{studentId}")
    public ResponseEntity<Faculty> getFacultyByStudent(@PathVariable Long studentId) {
        try {
            Faculty faculty = studentService.getFacultyByStudentId(studentId);
            return ResponseEntity.ok(faculty);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-faculty/{facultyId}")
    public ResponseEntity<List<Student>> getStudentsByFaculty(@PathVariable Long facultyId) {
        List<Student> students = studentService.getStudentsByFacultyId(facultyId);
        if (students.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(students);
    }


}

package ru.hogwarts.school.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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

    @GetMapping("{id}") //поиск студента по id
    public Student getStudentInfo(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @GetMapping("ageFilter/{age}")//поиск студента по возрасту
    public List<Student> ageFilter(@PathVariable int age) {
        return studentService.ageFilter(age);
    }

    @GetMapping//вывод всех студентов
    public List<Student> allStudents() {
        return studentService.allStudents();
    }

    @PostMapping//создать студента
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping//изменить студента
    public Student editStudent(@RequestBody Student student) {
        return studentService.editStudent(student);
    }

    @DeleteMapping("{id}")//удалить студента
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("filterByAge")//поиск студентов в определенном возрастном промежутке
    public List<Student> findByAgeBetweenStudent(@RequestParam("min") int min, @RequestParam("max") int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("/faculty/by-student/{studentId}")//поиск факультета по id студента
    public ResponseEntity<Faculty> getFacultyByStudent(@PathVariable Long studentId) {
        try {
            Faculty faculty = studentService.getFacultyByStudentId(studentId);
            return ResponseEntity.ok(faculty);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-faculty/{facultyId}")//поиск студентов по id факультета
    public ResponseEntity<List<Student>> getStudentsByFaculty(@PathVariable Long facultyId) {
        List<Student> students = studentService.getStudentsByFacultyId(facultyId);
        if (students.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(students);
    }

}

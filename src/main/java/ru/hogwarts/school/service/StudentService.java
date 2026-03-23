package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;
import java.util.List;

@Service
public class StudentService {
    private final HashMap<Long, Student> students = new HashMap<>();
    private Long lastId = 0L;

    public Student createStudent(Student student) {
        student.setId(++lastId);
        students.put(lastId, student);
        return student;
    }

    public Student editStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    public Student findStudent(Long id) {
        return students.get(id);
    }

    public Student deleteStudent(Long id) {
        return students.remove(id);
    }

    public List<Student> ageFilter(int age) {
        List<Student> studentsFilter = students.values().stream()
                .filter(student -> student.getAge() == age)
                .toList();
        return  studentsFilter;
    }

    public List<Student> allStudents(){
        List<Student> all = students.values().stream().toList();
        return all;
    }

}

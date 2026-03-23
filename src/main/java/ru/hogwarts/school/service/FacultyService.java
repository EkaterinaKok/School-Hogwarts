package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;
import java.util.List;

@Service
public class FacultyService {
    private final HashMap<Long, Faculty> faculties = new HashMap<>();
    private Long lastId = 0L;

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++lastId);
        faculties.put(lastId, faculty);
        return faculty;
    }

    public Faculty editFaculty(Faculty faculty) {
        if (faculties.containsKey(faculty.getId())) {
            faculties.put(faculty.getId(), faculty);
            return faculty;
        }
        return null;
    }

    public Faculty findFaculty(Long id) {
        return faculties.get(id);
    }

    public Faculty deleteFaculty(Long id) {
        return faculties.remove(id);
    }

    public List<Faculty> colorFilter(String color) {
        List<Faculty> foundColor = faculties.values().stream()
                .filter(faculty -> faculty.getColor().contains(color))
                .toList();
        return foundColor;
    }

    public List<Faculty> allFaculties(){
        List<Faculty> all = faculties.values().stream().toList();
        return all;
    }
}

package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public Faculty getFacultyInfo(@PathVariable Long id) {
        return facultyService.findFaculty(id);
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    public Faculty editFaculty(@RequestBody Faculty faculty) {
        return facultyService.editFaculty(faculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("colorFilter/{color}")
    public List<Faculty> colorFilter(@PathVariable String color) {
        return facultyService.colorFilter(color);
    }

    @GetMapping
    public List<Faculty> allFaculties() {
        return facultyService.allFaculties();
    }

    @GetMapping("filterByNameOrColor")
    public List<Faculty> findByNameOrColor(@RequestParam(required = false) String name, @RequestParam(required = false) String color){
        return facultyService.findByNameOfColorFaculties(name, color);
    }

}

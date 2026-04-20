package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private StudentService studentService;

    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private StudentController studentController;


    @Test
    void getStudentInfo_ShouldReturnStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Harry Potter");
        student.setAge(17);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));


        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getStudentInfo_ShouldReturnNotFound() throws Exception {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent_ShouldCreateNewStudent() throws Exception {
        Student savedStudent = new Student();
        savedStudent.setId(3L);
        savedStudent.setName("Neville Longbottom");
        savedStudent.setAge(16);

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Neville Longbottom\",\"age\":16}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Neville Longbottom"));
    }

    @Test
    void deleteStudent_ShouldDeleteStudent() throws Exception {
        doNothing().when(studentRepository).deleteById(1L);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());

        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllStudents_ShouldReturnEmptyListWhenNoStudents() throws Exception {
        when(studentRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

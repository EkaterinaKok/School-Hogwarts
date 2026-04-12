package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    void getFaculty_ShouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void createFaculty_ShouldCreateNewFaculty() throws Exception {
        Faculty savedFaculty = new Faculty();
        savedFaculty.setId(2L);
        savedFaculty.setName("Slytherin");
        savedFaculty.setColor("Green");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Slytherin\",\"color\":\"Green\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Slytherin"));
    }

    @Test
    void getFaculty_ShouldReturnNotFound() throws Exception {
        when(facultyRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/faculty/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllFaculties_ShouldReturnEmptyListWhenNoFaculties() throws Exception {
        when(facultyRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/faculty"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteFaculty_ShouldDeleteExistingFaculty() throws Exception {
        doNothing().when(facultyRepository).deleteById(1L);

        mockMvc.perform(delete("/faculty/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(facultyRepository, times(1)).deleteById(1L);
    }
}

package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetFacultyInfo() {
        // Создаём факультет
        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                "/faculty", faculty, Faculty.class);
        Long facultyId = createResponse.getBody().getId();

        // Получаем факультет по ID
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "/faculty/" + facultyId, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ravenclaw", response.getBody().getName());
        assertEquals("Blue", response.getBody().getColor());
    }

    @Test
    void testEditFaculty() {
        // Создаём факультет для редактирования
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                "/faculty", faculty, Faculty.class);

        // Редактируем факультет
        Faculty updatedFaculty = createResponse.getBody();
        updatedFaculty.setName("Gryffindor House");
        updatedFaculty.setColor("Scarlet");

        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updatedFaculty),
                Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gryffindor House", response.getBody().getName());
        assertEquals("Scarlet", response.getBody().getColor());
    }

    @Test
    void testDeleteFaculty() {
        // Создаём факультет для удаления
        Faculty faculty = new Faculty();
        faculty.setName("Slytherin");
        faculty.setColor("Green");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                "/faculty", faculty, Faculty.class);
        Long facultyId = createResponse.getBody().getId();

        // Удаляем факультет
        ResponseEntity<Void> response = restTemplate.exchange(
                "/faculty/" + facultyId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testColorFilter() {
        // Создаём факультеты разных цветов
        Faculty faculty1 = new Faculty();
        faculty1.setName("Red Faculty");
        faculty1.setColor("Red");
        restTemplate.postForEntity("/faculty", faculty1, Faculty.class);

        Faculty faculty2 = new Faculty();
        faculty2.setName("Blue Faculty");
        faculty2.setColor("Blue");
        restTemplate.postForEntity("/faculty", faculty2, Faculty.class);

        // Фильтруем по цвету "Red"
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/faculty/colorFilter/Red", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 1);
    }

    @Test
    void testAllFaculties() {
        ResponseEntity<List> response = restTemplate.getForEntity("/faculty", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindByNameOrColor() {
        // Создаём факультеты для поиска
        Faculty faculty1 = new Faculty();
        faculty1.setName("Ancient Magic");
        faculty1.setColor("Purple");
        restTemplate.postForEntity("/faculty", faculty1, Faculty.class);

        Faculty faculty2 = new Faculty();
        faculty2.setName("Modern Magic");
        faculty2.setColor("Orange");
        restTemplate.postForEntity("/faculty", faculty2, Faculty.class);

        // Поиск по имени "Ancient"
        ResponseEntity<List> nameResponse = restTemplate.getForEntity(
                "/faculty/filterByNameOrColor?name=Ancient", List.class);

        assertEquals(HttpStatus.OK, nameResponse.getStatusCode());
        assertNotNull(nameResponse.getBody());
        assertTrue(nameResponse.getBody().size() > 0);

        // Поиск по цвету "Orange"
        ResponseEntity<List> colorResponse = restTemplate.getForEntity(
                "/faculty/filterByNameOrColor?color=Orange", List.class);

        assertEquals(HttpStatus.OK, colorResponse.getStatusCode());
        assertNotNull(colorResponse.getBody());
        assertTrue(colorResponse.getBody().size() > 0);

        // Поиск по обоим параметрам
        ResponseEntity<List> bothResponse = restTemplate.getForEntity(
                "/faculty/filterByNameOrColor?name=Magic&color=Purple", List.class);

        assertEquals(HttpStatus.OK, bothResponse.getStatusCode());
        assertNotNull(bothResponse.getBody());
        assertTrue(bothResponse.getBody().size() > 0);
    }


    @Test
    void testGetFacultyInfoNotFound() {
        Long nonExistentId = 999999L;
        String url = "http://localhost:" + port + "/faculty/" + nonExistentId;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}


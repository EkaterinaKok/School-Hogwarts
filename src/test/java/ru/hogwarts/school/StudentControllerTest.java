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
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetStudentInfo() {
        // Создаём тестового студента
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(17);

        // Сохраняем студента
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                "/student", student, Student.class);
        Long studentId = createResponse.getBody().getId();

        // Получаем студента по ID
        ResponseEntity<Student> response = restTemplate.getForEntity(
                "/student/" + studentId, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Harry Potter", response.getBody().getName());
    }

    @Test
    void testAllStudents() {
        ResponseEntity<List> response = restTemplate.getForEntity("/student", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testEditStudent() {
        // Создаём студента
        Student student = new Student();
        student.setName("Draco Malfoy");
        student.setAge(17);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                "/student", student, Student.class);

        // Редактируем студента
        Student updatedStudent = createResponse.getBody();
        updatedStudent.setName("Draco Lucius Malfoy");

        ResponseEntity<Student> response = restTemplate.exchange(
                "/student", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updatedStudent),
                Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Draco Lucius Malfoy", response.getBody().getName());
    }

    @Test
    void testDeleteStudent() {
        // Создаём студента для удаления
        Student student = new Student();
        student.setName("Luna Lovegood");
        student.setAge(16);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                "/student", student, Student.class);
        Long studentId = createResponse.getBody().getId();

        // Удаляем студента
        ResponseEntity<Void> response = restTemplate.exchange(
                "/student/" + studentId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFindByAgeBetween() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/student/filterByAge?min=16&max=18", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetFacultyByStudent() {
        // Создаём факультет
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(
                "/faculty", faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаём студента с факультетом
        Student student = new Student();
        student.setName("Fred Weasley");
        student.setAge(18);
        student.setFaculty(facultyResponse.getBody());

        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(
                "/student", student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        // Получаем факультет по ID студента
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "/student/faculty/by-student/" + studentId, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gryffindor", response.getBody().getName());
    }

    @Test
    void testGetStudentsByFaculty() {
        // Создаём факультет
        Faculty faculty = new Faculty();
        faculty.setName("Slytherin");
        faculty.setColor("Green");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(
                "/faculty", faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаём студента на факультете
        Student student = new Student();
        student.setName("Severus Snape");
        student.setAge(40);
        student.setFaculty(facultyResponse.getBody());
        restTemplate.postForEntity("/student", student, Student.class);

        // Получаем студентов по ID факультета
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/student/by-faculty/" + facultyId, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

        @Test
    public void testGetNonExistentStudentReturns404() {
        Long nonExistentId = 999999L;
        String url = "http://localhost:" + port + "/student/" + nonExistentId;

        HttpClientErrorException exception = assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.getForObject(url, String.class),
                "Expected HttpClientErrorException to be thrown for non-existent student"
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

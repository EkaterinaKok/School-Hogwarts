package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void testGetStudents() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student", String.class);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testPostStudent() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(16);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student", student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void testCreateStudent() {
        // Arrange: готовим данные нового студента
        Student student = new Student();
        student.setName("Ron Weasley");
        student.setAge(16);

        // Act: отправляем POST‑запрос на создание
        ResponseEntity<Student> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );

        // Assert: проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Ron Weasley");
        assertThat(response.getBody().getAge()).isEqualTo(16);
    }

    @Test
    public void testAgeFilter() {
        // Arrange: создаём студентов разного возраста
        Student student1 = new Student();
        student1.setName("Harry Potter");
        student1.setAge(22);
        restTemplate.postForEntity("http://localhost:" + port + "/student", student1, Student.class);

        // Act: запрашиваем студентов 22 лет
        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/ageFilter/22",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetNonExistentStudentReturns404() {
        Long nonExistentId = 999999L;
        String url = "http://localhost:" + port + "/student/" + nonExistentId;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
package com.example.student_teacher.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.student_teacher.entity.Course;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setTitle("Introduction to Java");
        course.setCredit(3);
    }

    @Test
    void testSaveCourse() {
        // When
        Course savedCourse = courseRepository.save(course);

        // Then
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getTitle()).isEqualTo("Introduction to Java");
        assertThat(savedCourse.getCredit()).isEqualTo(3);
    }

    @Test
    void testFindAllCourses() {
        // Given
        courseRepository.save(course);
        Course course2 = new Course();
        course2.setTitle("Data Structures");
        course2.setCredit(4);
        courseRepository.save(course2);

        // When
        List<Course> courses = courseRepository.findAll();

        // Then
        assertThat(courses).hasSize(2);
    }

    @Test
    void testFindById() {
        // Given
        Course savedCourse = courseRepository.save(course);

        // When
        Optional<Course> foundCourse = courseRepository.findById(savedCourse.getId());

        // Then
        assertThat(foundCourse).isPresent();
        assertThat(foundCourse.get().getTitle()).isEqualTo("Introduction to Java");
    }

    @Test
    void testDeleteCourse() {
        // Given
        Course savedCourse = courseRepository.save(course);
        Long courseId = savedCourse.getId();

        // When
        courseRepository.deleteById(courseId);

        // Then
        Optional<Course> deletedCourse = courseRepository.findById(courseId);
        assertThat(deletedCourse).isEmpty();
    }
}

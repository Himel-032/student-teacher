package com.example.student_teacher.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.student_teacher.entity.Role;
import com.example.student_teacher.entity.Teacher;


@DataJpaTest
@ActiveProfiles("test")
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setName("Dr. Smith");
        teacher.setEmail("dr.smith@example.com");
        teacher.setPassword("password123");
    }

    @Test
    void testSaveTeacher() {
        // When
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Then
        assertThat(savedTeacher).isNotNull();
        assertThat(savedTeacher.getId()).isNotNull();
        assertThat(savedTeacher.getName()).isEqualTo("Dr. Smith");
        assertThat(savedTeacher.getEmail()).isEqualTo("dr.smith@example.com");
    }

    @Test
    void testFindByEmail() {
        // Given
        teacherRepository.save(teacher);

        // When
        Optional<Teacher> foundTeacher = teacherRepository.findByEmail("dr.smith@example.com");

        // Then
        assertThat(foundTeacher).isPresent();
        assertThat(foundTeacher.get().getName()).isEqualTo("Dr. Smith");
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<Teacher> foundTeacher = teacherRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundTeacher).isEmpty();
    }

    @Test
    void testDeleteTeacher() {
        // Given
        Teacher savedTeacher = teacherRepository.save(teacher);
        Long teacherId = savedTeacher.getId();

        // When
        teacherRepository.deleteById(teacherId);

        // Then
        Optional<Teacher> deletedTeacher = teacherRepository.findById(teacherId);
        assertThat(deletedTeacher).isEmpty();
    }

    @Test
    void testUpdateTeacher() {
        // Given
        Teacher savedTeacher = teacherRepository.save(teacher);

        // When
        savedTeacher.setName("Dr. Johnson");
        savedTeacher.setEmail("dr.johnson@example.com");
        Teacher updatedTeacher = teacherRepository.save(savedTeacher);

        // Then
        assertThat(updatedTeacher.getName()).isEqualTo("Dr. Johnson");
        assertThat(updatedTeacher.getEmail()).isEqualTo("dr.johnson@example.com");
    }

    @Test
    void testTeacherRole() {
        // Given
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Then
        assertThat(savedTeacher.getRole()).isEqualTo(Role.TEACHER);
    }
}

package com.example.student_teacher.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.student_teacher.entity.Dept;
import com.example.student_teacher.entity.Role;
import com.example.student_teacher.entity.Student;


@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DeptRepository deptRepository;

    private Student student;
    private Dept dept;

    @BeforeEach
    void setUp() {
        // Create and save department first
        dept = new Dept();
        dept.setName("Computer Science");
        dept = deptRepository.save(dept);

        // Create student
        student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setPassword("password123");
        student.setDept(dept);
    }

    @Test
    void testSaveStudent() {
        // When
        Student savedStudent = studentRepository.save(student);

        // Then
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo("John Doe");
        assertThat(savedStudent.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testFindByEmail() {
        // Given
        studentRepository.save(student);

        // When
        Optional<Student> foundStudent = studentRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<Student> foundStudent = studentRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundStudent).isEmpty();
    }

    @Test
    void testDeleteStudent() {
        // Given
        Student savedStudent = studentRepository.save(student);
        Long studentId = savedStudent.getId();

        // When
        studentRepository.deleteById(studentId);

        // Then
        Optional<Student> deletedStudent = studentRepository.findById(studentId);
        assertThat(deletedStudent).isEmpty();
    }

    @Test
    void testUpdateStudent() {
        // Given
        Student savedStudent = studentRepository.save(student);

        // When
        savedStudent.setName("Jane Doe");
        savedStudent.setEmail("jane.doe@example.com");
        Student updatedStudent = studentRepository.save(savedStudent);

        // Then
        assertThat(updatedStudent.getName()).isEqualTo("Jane Doe");
        assertThat(updatedStudent.getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void testStudentRole() {
        // Given
        Student savedStudent = studentRepository.save(student);

        // Then
        assertThat(savedStudent.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void testStudentDepartmentRelationship() {
        // When
        Student savedStudent = studentRepository.save(student);

        // Then
        assertThat(savedStudent.getDept()).isNotNull();
        assertThat(savedStudent.getDept().getName()).isEqualTo("Computer Science");
    }
}

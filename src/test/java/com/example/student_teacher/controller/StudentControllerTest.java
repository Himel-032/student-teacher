package com.example.student_teacher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Dept;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.DeptRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Student student;
    private Dept dept;
    private Course course;

    @BeforeEach
    void setUp() {
        // Clean up
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        deptRepository.deleteAll();

        // Create and save department
        dept = new Dept();
        dept.setName("Computer Science");
        dept = deptRepository.save(dept);

        // Create and save student
        student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setPassword(passwordEncoder.encode("password123"));
        student.setDept(dept);
        student = studentRepository.save(student);

        // Create and save course
        course = new Course();
        course.setTitle("Java Programming");
        course.setCredit(3);
        course = courseRepository.save(course);
    }

    @Test
    void testGetProfile() throws Exception {
        // When & Then
        mockMvc.perform(get("/student/profile")
                .with(user("john.doe@example.com").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        Student updatedStudent = new Student();
        updatedStudent.setName("Jane Doe");
        updatedStudent.setPassword("newPassword");

        // When & Then
        mockMvc.perform(put("/student/profile")
                .with(user("john.doe@example.com").roles("STUDENT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDepartments() throws Exception {
        // When & Then
        mockMvc.perform(get("/student/departments")
                .with(user("john.doe@example.com").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testTakeCourse() throws Exception {
        // When & Then
        mockMvc.perform(post("/student/courses/" + course.getId())
                .with(user("john.doe@example.com").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveCourse() throws Exception {
        // Given - add course first
        mockMvc.perform(post("/student/courses/" + course.getId())
                .with(user("john.doe@example.com").roles("STUDENT"))
                .with(csrf()));

        // When & Then
        mockMvc.perform(delete("/student/courses/" + course.getId())
                .with(user("john.doe@example.com").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().isOk());
    }
}

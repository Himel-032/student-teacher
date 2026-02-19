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

import java.util.List;
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
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;
import com.example.student_teacher.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Teacher teacher;
    private Course course;

    @BeforeEach
    void setUp() {
        // Clean up
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();

        // Create and save teacher
        teacher = new Teacher();
        teacher.setName("Dr. Smith");
        teacher.setEmail("dr.smith@example.com");
        teacher.setPassword(passwordEncoder.encode("password123"));
        teacher = teacherRepository.save(teacher);

        // Create and save course
        course = new Course();
        course.setTitle("Java Programming");
        course.setCredit(3);
        course = courseRepository.save(course);
    }

    @Test
    void testGetProfile() throws Exception {
        // When & Then
        mockMvc.perform(get("/teacher/profile")
                .with(user("dr.smith@example.com").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Smith"))
                .andExpect(jsonPath("$.email").value("dr.smith@example.com"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setName("Dr. Johnson");
        updatedTeacher.setPassword("newPassword");

        // When & Then
        mockMvc.perform(put("/teacher/profile")
                .with(user("dr.smith@example.com").roles("TEACHER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTeacher)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCourses() throws Exception {
        // When & Then
        mockMvc.perform(get("/teacher/courses")
                .with(user("dr.smith@example.com").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateCourse() throws Exception {
        // When & Then
        mockMvc.perform(post("/teacher/courses")
                .with(user("dr.smith@example.com").roles("TEACHER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCourse() throws Exception {
        Course updatedCourse = new Course();
        updatedCourse.setTitle("Advanced Java");
        updatedCourse.setCredit(4);

        // When & Then
        mockMvc.perform(put("/teacher/courses/" + course.getId())
                .with(user("dr.smith@example.com").roles("TEACHER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCourse() throws Exception {
        // When & Then
        mockMvc.perform(delete("/teacher/courses/" + course.getId())
                .with(user("dr.smith@example.com").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().isOk());
    }
}

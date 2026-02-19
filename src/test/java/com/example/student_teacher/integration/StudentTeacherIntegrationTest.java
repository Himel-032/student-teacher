package com.example.student_teacher.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.DeptRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentTeacherIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Student student;
    private Teacher teacher;
    private Course course;
    private Dept dept;

    @BeforeEach
    void setUp() {
        // Clean up
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        deptRepository.deleteAll();

        // Create department
        dept = new Dept();
        dept.setName("Computer Science");
        dept = deptRepository.save(dept);

        // Create student
        student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setPassword(passwordEncoder.encode("password123"));
        student.setDept(dept);
        student = studentRepository.save(student);

        // Create teacher
        teacher = new Teacher();
        teacher.setName("Dr. Smith");
        teacher.setEmail("dr.smith@example.com");
        teacher.setPassword(passwordEncoder.encode("password123"));
        teacher = teacherRepository.save(teacher);

        // Create course
        course = new Course();
        course.setTitle("Java Programming");
        course.setCredit(3);
        course = courseRepository.save(course);
    }

    @Test
    void testStudentCanGetProfile() throws Exception {
        mockMvc.perform(get("/student/profile")
                .with(user(student.getEmail()).roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testStudentCanUpdateProfile() throws Exception {
        Student updatedStudent = new Student();
        updatedStudent.setName("Jane Doe");

        mockMvc.perform(put("/student/profile")
                .with(user(student.getEmail()).roles("STUDENT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));

        // Verify in database
        Student dbStudent = studentRepository.findByEmail(student.getEmail()).orElseThrow();
        assertThat(dbStudent.getName()).isEqualTo("Jane Doe");
    }

    @Test
    void testStudentCanEnrollInCourse() throws Exception {
        mockMvc.perform(post("/student/courses/" + course.getId())
                .with(user(student.getEmail()).roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().isOk());

        // Verify in database
        Student dbStudent = studentRepository.findById(student.getId()).orElseThrow();
        assertThat(dbStudent.getCourses()).hasSize(1);
        assertThat(dbStudent.getCourses().iterator().next().getTitle()).isEqualTo("Java Programming");
    }

    @Test
    void testTeacherCanGetProfile() throws Exception {
        mockMvc.perform(get("/teacher/profile")
                .with(user(teacher.getEmail()).roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Smith"))
                .andExpect(jsonPath("$.email").value("dr.smith@example.com"));
    }

    @Test
    void testTeacherCanCreateCourse() throws Exception {
        Course newCourse = new Course();
        newCourse.setTitle("Data Structures");
        newCourse.setCredit(4);

        mockMvc.perform(post("/teacher/courses")
                .with(user(teacher.getEmail()).roles("TEACHER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isOk());

        // Verify in database
        assertThat(courseRepository.findAll()).hasSize(2);
    }

    @Test
    void testTeacherCanUpdateCourse() throws Exception {
        Course updatedCourse = new Course();
        updatedCourse.setTitle("Advanced Java Programming");
        updatedCourse.setCredit(4);

        mockMvc.perform(put("/teacher/courses/" + course.getId())
                .with(user(teacher.getEmail()).roles("TEACHER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isOk());

        // Verify in database
        Course dbCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(dbCourse.getTitle()).isEqualTo("Advanced Java Programming");
        assertThat(dbCourse.getCredit()).isEqualTo(4);
    }

    @Test
    void testStudentCanGetDepartments() throws Exception {
        mockMvc.perform(get("/student/departments")
                .with(user(student.getEmail()).roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Computer Science"));
    }

    @Test
    void testTeacherCanGetAllCourses() throws Exception {
        mockMvc.perform(get("/teacher/courses")
                .with(user(teacher.getEmail()).roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Java Programming"));
    }

    @Test
    void testContextLoads() {
        assertThat(studentRepository).isNotNull();
        assertThat(teacherRepository).isNotNull();
        assertThat(courseRepository).isNotNull();
        assertThat(deptRepository).isNotNull();
    }
}

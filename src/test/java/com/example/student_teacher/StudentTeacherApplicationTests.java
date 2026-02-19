package com.example.student_teacher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.DeptRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;

@SpringBootTest
@ActiveProfiles("test")
class StudentTeacherApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void testRepositoriesAreLoaded() {
        assertThat(studentRepository).isNotNull();
        assertThat(teacherRepository).isNotNull();
        assertThat(courseRepository).isNotNull();
        assertThat(deptRepository).isNotNull();
    }

}

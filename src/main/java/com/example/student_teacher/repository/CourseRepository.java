package com.example.student_teacher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Teacher;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacher(Teacher teacher);
}

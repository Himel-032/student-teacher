package com.example.student_teacher.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.student_teacher.entity.Teacher;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
}

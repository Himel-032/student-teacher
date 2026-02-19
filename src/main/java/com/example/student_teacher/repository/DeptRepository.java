package com.example.student_teacher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.student_teacher.entity.Dept;

public interface DeptRepository extends JpaRepository<Dept, Long> {
}

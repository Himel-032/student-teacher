package com.example.student_teacher.security;


import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public CustomUserDetailsService(StudentRepository studentRepository,
                                    TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // 1️⃣ Check STUDENT table
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return User.builder()
                    .username(student.get().getEmail())
                    .password(student.get().getPassword())
                    .roles("STUDENT")   // → ROLE_STUDENT
                    .build();
        }

        // 2️⃣ Check TEACHER table
        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            return User.builder()
                    .username(teacher.get().getEmail())
                    .password(teacher.get().getPassword())
                    .roles("TEACHER")   // → ROLE_TEACHER
                    .build();
        }

        // 3️⃣ Not found
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

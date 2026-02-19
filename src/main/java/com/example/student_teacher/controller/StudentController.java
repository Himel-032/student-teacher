package com.example.student_teacher.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Dept;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.DeptRepository;
import com.example.student_teacher.repository.StudentRepository;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final DeptRepository deptRepo;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentRepository studentRepo, CourseRepository courseRepo,
            DeptRepository deptRepo, PasswordEncoder passwordEncoder) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.deptRepo = deptRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            Student student = studentRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profile: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Student updatedStudent, Principal principal) {
        try {
            Student student = studentRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Update name if provided
            if (updatedStudent.getName() != null && !updatedStudent.getName().isEmpty()) {
                student.setName(updatedStudent.getName());
            }

            // Update password if provided
            if (updatedStudent.getPassword() != null && !updatedStudent.getPassword().isEmpty()) {
                student.setPassword(passwordEncoder.encode(updatedStudent.getPassword()));
            }

            // Update department if provided
            if (updatedStudent.getDept() != null && updatedStudent.getDept().getId() != null) {
                Dept dept = deptRepo.findById(updatedStudent.getDept().getId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                student.setDept(dept);
            }

            Student saved = studentRepo.save(student);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments() {
        return ResponseEntity.ok(deptRepo.findAll());
    }

    @PostMapping("/courses/{courseId}")
    public String takeCourse(@PathVariable Long courseId, Principal principal) {
        Student student = studentRepo.findByEmail(principal.getName()).get();
        Course course = courseRepo.findById(courseId).get();
        student.getCourses().add(course);
        studentRepo.save(student);
        return "Course taken";
    }

    @DeleteMapping("/courses/{courseId}")
    public String removeCourse(@PathVariable Long courseId, Principal principal) {
        Student student = studentRepo.findByEmail(principal.getName()).get();
        student.getCourses().removeIf(c -> c.getId().equals(courseId));
        studentRepo.save(student);
        return "Course removed";
    }
}

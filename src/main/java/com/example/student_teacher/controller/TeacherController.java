package com.example.student_teacher.controller;

import java.security.Principal;
import java.util.List;

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
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.DeptRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final DeptRepository deptRepo;
    private final PasswordEncoder passwordEncoder;

    public TeacherController(CourseRepository courseRepo, TeacherRepository teacherRepo,
            StudentRepository studentRepo, DeptRepository deptRepo, PasswordEncoder passwordEncoder) {
        this.courseRepo = courseRepo;
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.deptRepo = deptRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @PostMapping("/courses")
    public Course addCourse(@RequestBody Course course, Principal principal) {
        Teacher teacher = teacherRepo.findByEmail(principal.getName()).get();
        course.setTeacher(teacher);
        return courseRepo.save(course);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        try {
            Course course = courseRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            if (updatedCourse.getTitle() != null && !updatedCourse.getTitle().isEmpty()) {
                course.setTitle(updatedCourse.getTitle());
            }
            if (updatedCourse.getCredit() > 0) {
                course.setCredit(updatedCourse.getCredit());
            }

            Course saved = courseRepo.save(course);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating course: " + e.getMessage());
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            if (!courseRepo.existsById(id)) {
                return ResponseEntity.badRequest().body("Course not found");
            }
            courseRepo.deleteById(id);
            return ResponseEntity.ok("Course deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting course: " + e.getMessage());
        }
    }

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            Student student = studentRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching student: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            Teacher teacher = teacherRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            return ResponseEntity.ok(teacher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profile: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Teacher updatedTeacher, Principal principal) {
        try {
            Teacher teacher = teacherRepo.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            // Update name if provided
            if (updatedTeacher.getName() != null && !updatedTeacher.getName().isEmpty()) {
                teacher.setName(updatedTeacher.getName());
            }

            // Update password if provided
            if (updatedTeacher.getPassword() != null && !updatedTeacher.getPassword().isEmpty()) {
                teacher.setPassword(passwordEncoder.encode(updatedTeacher.getPassword()));
            }

            Teacher saved = teacherRepo.save(teacher);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    @GetMapping("/departments")
    public List<Dept> getAllDepartments() {
        return deptRepo.findAll();
    }

    @PostMapping("/students")
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            // Check if email already exists
            if (studentRepo.findByEmail(student.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists");
            }

            // Set department if provided
            if (student.getDept() != null && student.getDept().getId() != null) {
                Dept dept = deptRepo.findById(student.getDept().getId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                student.setDept(dept);
            }

            // Encode password and save
            student.setPassword(passwordEncoder.encode(student.getPassword()));
            Student savedStudent = studentRepo.save(student);
            return ResponseEntity.ok(savedStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding student: " + e.getMessage());
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            if (!studentRepo.existsById(id)) {
                return ResponseEntity.badRequest().body("Student not found");
            }
            studentRepo.deleteById(id);
            return ResponseEntity.ok("Student deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting student: " + e.getMessage());
        }
    }
}

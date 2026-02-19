package com.example.student_teacher.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;

@Controller
public class WebController {

    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final CourseRepository courseRepo;

    public WebController(StudentRepository studentRepo, TeacherRepository teacherRepo, CourseRepository courseRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
        this.courseRepo = courseRepo;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            return "redirect:/student/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            return "redirect:/teacher/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication auth, Model model) {
        Student student = studentRepo.findByEmail(auth.getName()).orElse(null);
        List<Course> allCourses = courseRepo.findAll();

        model.addAttribute("student", student);
        model.addAttribute("myCourses", student != null ? student.getCourses() : List.of());
        model.addAttribute("allCourses", allCourses);
        return "student-dashboard";
    }

    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(Authentication auth, Model model) {
        Teacher teacher = teacherRepo.findByEmail(auth.getName()).orElse(null);
        List<Course> myCourses = courseRepo.findByTeacher(teacher);
        int totalCredits = myCourses.stream().mapToInt(Course::getCredit).sum();

        model.addAttribute("teacher", teacher);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("totalCredits", totalCredits);
        return "teacher-dashboard";
    }

    @PostMapping("/teacher/courses/add")
    public String addCourse(@RequestParam String title, @RequestParam int credit, Authentication auth) {
        Teacher teacher = teacherRepo.findByEmail(auth.getName()).orElse(null);
        if (teacher != null) {
            Course course = new Course();
            course.setTitle(title);
            course.setCredit(credit);
            course.setTeacher(teacher);
            courseRepo.save(course);
        }
        return "redirect:/teacher/dashboard";
    }

    @PostMapping("/student/courses/enroll/{courseId}")
    public String enrollCourse(@PathVariable Long courseId, Authentication auth) {
        Student student = studentRepo.findByEmail(auth.getName()).orElse(null);
        Course course = courseRepo.findById(courseId).orElse(null);
        if (student != null && course != null) {
            student.getCourses().add(course);
            studentRepo.save(student);
        }
        return "redirect:/student/dashboard";
    }

    @PostMapping("/student/courses/drop/{courseId}")
    public String dropCourse(@PathVariable Long courseId, Authentication auth) {
        Student student = studentRepo.findByEmail(auth.getName()).orElse(null);
        if (student != null) {
            student.getCourses().removeIf(c -> c.getId().equals(courseId));
            studentRepo.save(student);
        }
        return "redirect:/student/dashboard";
    }

    @PostMapping("/teacher/courses/delete/{courseId}")
    public String deleteCourse(@PathVariable Long courseId, Authentication auth) {
        Teacher teacher = teacherRepo.findByEmail(auth.getName()).orElse(null);
        Course course = courseRepo.findById(courseId).orElse(null);

        // Only allow deletion if course belongs to the logged-in teacher
        if (teacher != null && course != null && course.getTeacher().getId().equals(teacher.getId())) {
            // Remove course from all enrolled students first
            List<Student> students = studentRepo.findAll();
            for (Student student : students) {
                student.getCourses().removeIf(c -> c.getId().equals(courseId));
                studentRepo.save(student);
            }
            // Then delete the course
            courseRepo.delete(course);
        }
        return "redirect:/teacher/dashboard";
    }
}

-- =====================================================
-- Initial Data for Student-Teacher Application
-- =====================================================
-- Password for all users: "password" (BCrypt encoded)
-- BCrypt hash: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
-- =====================================================

-- Clear existing data (in correct order due to foreign keys)
DELETE FROM student_courses;
DELETE FROM course;
DELETE FROM student;
DELETE FROM teacher;
DELETE FROM dept;

-- =====================================================
-- Insert Departments
-- =====================================================
INSERT INTO dept (id, name) VALUES 
(1, 'Computer Science'),
(2, 'Mathematics'),
(3, 'Physics'),
(4, 'English'),
(5, 'Arts');

-- =====================================================
-- Insert Teachers
-- =====================================================
INSERT INTO teacher (id, name, email, password, role) VALUES 
(1, 'John Smith', 'teacher@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'TEACHER'),
(2, 'Sarah Johnson', 'sarah@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'TEACHER'),
(3, 'Michael Brown', 'michael@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'TEACHER');

-- =====================================================
-- Insert Courses (linked to teachers)
-- =====================================================
INSERT INTO course (id, title, credit, teacher_id, dept_id) VALUES 
(1, 'Mathematics 101', 3, 1, 2),
(2, 'Physics Fundamentals', 4, 1, 3),
(3, 'English Literature', 3, 2, 4),
(4, 'Computer Science Basics', 4, 2, 1),
(5, 'History of Art', 2, 3, 5),
(6, 'Chemistry Lab', 3, 3, 3);

-- =====================================================
-- Insert Students
-- =====================================================
INSERT INTO student (id, name, email, password, role, dept_id) VALUES 
(1, 'Jane Doe', 'student@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'STUDENT', 1),
(2, 'Bob Wilson', 'bob@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'STUDENT', 2),
(3, 'Alice Chen', 'alice@mail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'STUDENT', 1);

-- =====================================================
-- Enroll Students in Courses
-- =====================================================
INSERT INTO student_courses (student_id, course_id) VALUES 
(1, 1),  -- Jane enrolled in Mathematics
(1, 3),  -- Jane enrolled in English Literature
(2, 2),  -- Bob enrolled in Physics
(2, 4),  -- Bob enrolled in Computer Science
(3, 1),  -- Alice enrolled in Mathematics
(3, 5);  -- Alice enrolled in History of Art

-- =====================================================
-- Update sequences to avoid ID conflicts
-- =====================================================
SELECT setval('teacher_seq', 100);
SELECT setval('student_seq', 100);
SELECT setval('course_seq', 100);
SELECT setval('dept_seq', 100);

-- =====================================================
-- Test Credentials:
-- =====================================================
-- Student: student@mail.com / password
-- Student: bob@mail.com / password
-- Student: alice@mail.com / password
-- Teacher: teacher@mail.com / password
-- Teacher: sarah@mail.com / password
-- Teacher: michael@mail.com / password
-- =====================================================

--    docker-compose down; docker-compose up -d; Start-Sleep -Seconds 12; docker cp init.sql student_teacher-postgres-1:/tmp/init.sql; docker exec student_teacher-postgres-1 psql -U admin -d university -f /tmp/init.sql

-- mvn clean package -DskipTests
package com.example.student_teacher.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int credit;

    @ManyToOne
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Dept dept;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getCredit() {
        return credit;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Dept getDept() {
        return dept;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }
}

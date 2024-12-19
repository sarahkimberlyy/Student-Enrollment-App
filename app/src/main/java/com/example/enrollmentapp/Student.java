package com.example.enrollmentapp;

import java.util.List;

public class Student {
    private String email;
    private List<Subject> enrolledSubjects;
    private int totalCredits;

    public Student() {
        // Default constructor required for Firestore
    }

    public Student(String email, List<Subject> enrolledSubjects, int totalCredits) {
        this.email = email;
        this.enrolledSubjects = enrolledSubjects;
        this.totalCredits = totalCredits;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Subject> getEnrolledSubjects() {
        return enrolledSubjects;
    }

    public void setEnrolledSubjects(List<Subject> enrolledSubjects) {
        this.enrolledSubjects = enrolledSubjects;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }
}

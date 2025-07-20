package com.java_web.backend.Common.DTO;

import java.util.Objects;

public class InitialSyllabusRequest {
    private String courseName;
    private String teacherName;
    private String term;
    private String description;

    public InitialSyllabusRequest() {}

    public InitialSyllabusRequest(String courseName, String teacherName, String term, String description) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.term = term;
        this.description = description;
    }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InitialSyllabusRequest that = (InitialSyllabusRequest) o;
        return Objects.equals(courseName, that.courseName) &&
                Objects.equals(teacherName, that.teacherName) &&
                Objects.equals(term, that.term) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseName, teacherName, term, description);
    }

    @Override
    public String toString() {
        return "InitialSyllabusRequest{" +
                "courseName='" + courseName + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", term='" + term + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 
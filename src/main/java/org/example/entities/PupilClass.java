package org.example.entities;

public class PupilClass {
    private int id;
    private int grade;
    private String name;

    public PupilClass() {
    }

    public PupilClass(int id, int grade, String name) {
        this.id = id;
        this.grade = grade;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package org.example.entities;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PupilClass that = (PupilClass) o;
        return id == that.id && grade == that.grade && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, grade, name);
    }

    @Override
    public String toString() {
        return "PupilClass{" +
                "id=" + id +
                ", grade=" + grade +
                ", name='" + name + '\'' +
                '}';
    }
}

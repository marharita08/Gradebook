package org.example.entities;

import java.util.Objects;

public class Theme {
    private int id;
    private SubjectDetails subjectDetails;
    private String name;

    public Theme(SubjectDetails subjectDetails) {
        this.subjectDetails = subjectDetails;
    }

    public Theme() {
    }

    public Theme(int id, SubjectDetails subjectDetails, String name) {
        this.id = id;
        this.subjectDetails = subjectDetails;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubjectDetails getSubjectDetails() {
        return subjectDetails;
    }

    public void setSubjectDetails(SubjectDetails subjectDetails) {
        this.subjectDetails = subjectDetails;
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
        Theme theme = (Theme) o;
        return id == theme.id && subjectDetails.equals(theme.subjectDetails) && name.equals(theme.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subjectDetails, name);
    }

    @Override
    public String toString() {
        return "Theme{" +
                "id=" + id +
                ", subjectDetails=" + subjectDetails +
                ", name='" + name + '\'' +
                '}';
    }
}

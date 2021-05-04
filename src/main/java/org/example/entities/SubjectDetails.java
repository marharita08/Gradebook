package org.example.entities;

import java.util.Objects;

public class SubjectDetails {
    private int id;
    private PupilClass pupilClass;
    private Teacher teacher;
    private Subject subject;

    public SubjectDetails(int id, PupilClass pupilClass, Teacher teacher, Subject subject) {
        this.id = id;
        this.pupilClass = pupilClass;
        this.teacher = teacher;
        this.subject = subject;
    }

    public SubjectDetails() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectDetails that = (SubjectDetails) o;
        return id == that.id && pupilClass.equals(that.pupilClass) && Objects.equals(teacher, that.teacher) && subject.equals(that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pupilClass, teacher, subject);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PupilClass getPupilClass() {
        return pupilClass;
    }

    public void setPupilClass(PupilClass pupilClass) {
        this.pupilClass = pupilClass;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "SubjectDetails{" +
                "id=" + id +
                ", pupilClass=" + pupilClass +
                ", teacher=" + teacher +
                ", subject=" + subject +
                '}';
    }
}

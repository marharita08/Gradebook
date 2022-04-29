package org.example.entities;

import java.util.Objects;

public class Mark {
    private int id;
    private Pupil pupil;
    private Lesson lesson;
    private String mark;

    public Mark(int id, Pupil pupil, Lesson lesson, String mark) {
        this.id = id;
        this.pupil = pupil;
        this.lesson = lesson;
        this.mark = mark;
    }

    public Mark() {
    }

    public Mark(Lesson lesson) {
        this.lesson = lesson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pupil getPupil() {
        return pupil;
    }

    public void setPupil(Pupil pupil) {
        this.pupil = pupil;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "id=" + id +
                ", pupil=" + pupil +
                ", lesson=" + lesson +
                ", mark=" + mark +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark1 = (Mark) o;
        return id == mark1.id && mark == mark1.mark && pupil.equals(mark1.pupil) && lesson.equals(mark1.lesson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pupil, lesson, mark);
    }
}

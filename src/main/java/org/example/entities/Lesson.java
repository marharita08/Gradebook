package org.example.entities;


import java.sql.Date;
import java.util.Objects;

public class Lesson {
    private int id;
    private Theme theme;
    private Date date;
    private String topic;

    public Lesson(int id, Theme theme, Date date, String topic) {
        this.id = id;
        this.theme = theme;
        this.date = date;
        this.topic = topic;
    }

    public Lesson() {
    }

    public Lesson(Theme theme) {
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", theme=" + theme +
                ", date=" + date +
                ", topic='" + topic + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return id == lesson.id && theme.equals(lesson.theme) && date.equals(lesson.date) && topic.equals(lesson.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, theme, date, topic);
    }
}

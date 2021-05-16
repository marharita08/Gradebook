package org.example.entities;


import java.sql.Date;
import java.util.Objects;

public class Lesson {
    private int id;
    private SubjectDetails subjectDetails;
    private Date date;
    private String topic;

    public Lesson(int id, SubjectDetails subjectDetails, Date date, String topic) {
        this.id = id;
        this.subjectDetails = subjectDetails;
        this.date = date;
        this.topic = topic;
    }

    public Lesson() {
    }

    public Lesson(SubjectDetails subjectDetails) {
        this.subjectDetails = subjectDetails;
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
                ", subjectDetails=" + subjectDetails +
                ", date=" + date +
                ", topic='" + topic + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return id == lesson.id && subjectDetails.equals(lesson.subjectDetails) && date.equals(lesson.date) && topic.equals(lesson.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subjectDetails, date, topic);
    }
}

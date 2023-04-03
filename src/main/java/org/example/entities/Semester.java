package org.example.entities;

import java.sql.Date;
import java.util.Objects;

public class Semester {
    private int id;
    private SchoolYear schoolYear;
    private String name;
    private Date startDate;
    private Date endDate;

    public Semester() {
    }

    public Semester(int id, SchoolYear schoolYear, String name, Date startDate, Date endDate) {
        this.id = id;
        this.schoolYear = schoolYear;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SchoolYear getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(SchoolYear schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Semester semester = (Semester) o;
        return id == semester.id && schoolYear.equals(semester.schoolYear) && name.equals(semester.name) && startDate.equals(semester.startDate) && endDate.equals(semester.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, schoolYear, name, startDate, endDate);
    }

    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", schoolYear=" + schoolYear +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}

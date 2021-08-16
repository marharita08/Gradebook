package org.example.entities;

import java.util.Objects;

public class Teacher {
    private int id;
    private String name;
    private String position;

    public Teacher(int id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public Teacher(){
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public Teacher(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return id == teacher.id && name.equals(teacher.name) && Objects.equals(position, teacher.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, position);
    }
}

package org.example.entities;

import java.util.Objects;

public class Pupil {
    private int id;
    private String name;
    private PupilClass pupilClass;

    public Pupil(int id, String name, PupilClass pupilClass) {
        this.id = id;
        this.pupilClass = pupilClass;
        this.name = name;
    }

    public Pupil() {
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

    public PupilClass getPupilClass() {
        return pupilClass;
    }

    public void setPupilClass(PupilClass pupilClass) {
        this.pupilClass = pupilClass;
    }

    @Override
    public String toString() {
        return "Pupil{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pupilClass=" + pupilClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pupil pupil = (Pupil) o;
        return id == pupil.id && name.equals(pupil.name) && Objects.equals(pupilClass, pupil.pupilClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pupilClass);
    }
}

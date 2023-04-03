package org.example.entities;

import java.util.Objects;

public class Pupil extends User {
    private String name;
    private PupilClass pupilClass;

    public Pupil(int id, String name, PupilClass pupilClass, String photo) {
        this.id = id;
        this.pupilClass = pupilClass;
        this.name = name;
        this.photo = photo;
    }

    public Pupil() {
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

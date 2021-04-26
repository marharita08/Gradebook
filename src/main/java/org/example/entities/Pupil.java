package org.example.entities;

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
}

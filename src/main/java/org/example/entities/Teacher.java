package org.example.entities;

public class Teacher {
    private int id;
    private String name;
    private String position;
    private Teacher chief;

    public Teacher(int id, String name, String position, Teacher chief) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.chief = chief;
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

    public Teacher getChief() {
        return chief;
    }

    public void setChief(Teacher chief) {
        this.chief = chief;
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
                ", chief=" + chief +
                '}';
    }
}

package org.example.dao;

import org.example.entities.Teacher;

import java.util.List;

public interface TeacherDAO {
    List<Teacher> getAllTeachers();
    Teacher getTeacher(int id);
    void addTeacher(Teacher teacher);
    void updateTeacher(Teacher teacher);
    void deleteTeacher(int id);
    List<Teacher> getEnableChiefs(int id);

}

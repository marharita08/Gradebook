package org.example.dao.interfaces;

import org.example.entities.Teacher;

import java.util.List;

public interface TeacherDAO {
    /**
     * Read all teachers from database and put them into list.
     * @return List<Teacher>
     */
    List<Teacher> getAllTeachers(String dbName);

    /**
     * Read teacher from database by id.
     * @param id teacher's id
     * @return Teacher
     */
    Teacher getTeacher(int id, String dbName);

    /**
     * Insert new teacher into database.
     * @param teacher adding teacher
     */
    void addTeacher(Teacher teacher, String dbName);

    /**
     * Update teacher's data into database.
     * @param teacher editing teacher
     */
    void updateTeacher(Teacher teacher, String dbName);

    /**
     * Delete teacher from database.
     * @param id teacher's id
     */
    void deleteTeacher(int id, String dbName);

    /**
     * Get total count of teachers from database.
     * @return int
     */
    int getCountOfTeachers(String dbName);

    /**
     * Get teacher list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Teacher>
     */
    List<Teacher> getTeachersByPage(int page, int range, String dbName);

    /**
     * Search teachers by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Teacher>
     * @throws Exception if set parameter is wrong
     */
    List<Teacher> searchTeachers(String val, String param, String dbName) throws Exception;
}

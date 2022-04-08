package org.example.dao.interfaces;

import org.example.entities.Teacher;

import java.util.List;

public interface TeacherDAO {
    /**
     * Read all teachers from database and put them into list.
     * @return List<Teacher>
     */
    List<Teacher> getAllTeachers();

    /**
     * Read teacher from database by id.
     * @param id teacher's id
     * @return Teacher
     */
    Teacher getTeacher(int id);

    /**
     * Insert new teacher into database.
     * @param teacher adding teacher
     */
    void addTeacher(Teacher teacher);

    /**
     * Update teacher's data into database.
     * @param teacher editing teacher
     */
    void updateTeacher(Teacher teacher);

    /**
     * Delete teacher from database.
     * @param id teacher's id
     */
    void deleteTeacher(int id);

    /**
     * Get list of teachers who teach some subject in class with set id.
     * @param id class id
     * @return List<Teacher>
     */
    List<Teacher> getTeachersByPupilClass(int id);

    /**
     * Get list of teachers who teach subject with set id.
     * @param id subject id
     * @return List<Teacher>
     */
    List<Teacher> getTeachersBySubject(int id);

    /**
     * Get total count of teachers from database.
     * @return int
     */
    int getCountOfTeachers();

    /**
     * Get teacher list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Teacher>
     */
    List<Teacher> getTeachersByPage(int page, int range);

    /**
     * Search teachers by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Teacher>
     * @throws Exception if set parameter is wrong
     */
    List<Teacher> searchTeachers(String val, String param) throws Exception;
}

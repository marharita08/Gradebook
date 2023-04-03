package org.example.dao.interfaces;

import org.example.entities.Subject;

import java.util.List;

public interface SubjectDAO {
    /**
     * Read all subjects from database and put them into list.
     * @return List<Subject>
     */
    List<Subject> getAllSubjects(String dbName);

    /**
     * Read subject from database by id.
     * @param id subject id
     * @return Subject
     */
    Subject getSubject(int id, String dbName);

    /**
     * Insert new subject into database.
     * @param subject adding subject
     */
    void addSubject(Subject subject, String dbName);

    /**
     * Update subject data into database.
     * @param subject editing subject
     */
    void updateSubject(Subject subject, String dbName);

    /**
     * Delete subject from database.
     * @param id subject id
     */
    void deleteSubject(int id, String dbName);

    /**
     * Get subjects that are learned by class with set id.
     * @param id class id
     * @return List<Subject>
     */
    List<Subject> getSubjectsByPupilClass(int id, String dbName);

    /**
     * Get subjects which are teacher by teacher with set id
     * @param id teacher id
     * @return List<Subject>
     */
    List<Subject> getSubjectsByTeacher(int id, String dbName);

    /**
     * Get total count of subject from database.
     * @return int
     */
    int getCountOfSubjects(String dbName);

    /**
     * Get subject list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Subject>
     */
    List<Subject> getSubjectsByPage(int page, int range, String dbName);

    /**
     * Search subjects by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Subject>
     * @throws Exception if set parameter is wrong
     */
    List<Subject> searchSubjects(String val, String param, String dbName) throws Exception;
}

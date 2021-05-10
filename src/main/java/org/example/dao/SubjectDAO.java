package org.example.dao;

import org.example.entities.Subject;

import java.util.List;

public interface SubjectDAO {
    /**
     * Read all subjects from database and put them into list.
     * @return List<Subject>
     */
    List<Subject> getAllSubjects();

    /**
     * Read subject from database by id.
     * @param id subject id
     * @return Subject
     */
    Subject getSubject(int id);

    /**
     * Insert new subject into database.
     * @param subject adding subject
     */
    void addSubject(Subject subject);

    /**
     * Update subject data into database.
     * @param subject editing subject
     */
    void updateSubject(Subject subject);

    /**
     * Delete subject from database.
     * @param id subject id
     */
    void deleteSubject(int id);

    /**
     * Get subjects that are learned by class with set id.
     * @param id class id
     * @return List<Subject>
     */
    List<Subject> getSubjectByPupilClass(int id);

    /**
     * Get subjects which are teacher by teacher with set id
     * @param id teacher id
     * @return List<Subject>
     */
    List<Subject> getSubjectByTeacher(int id);
}

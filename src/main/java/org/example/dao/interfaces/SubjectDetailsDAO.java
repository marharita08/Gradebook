package org.example.dao.interfaces;

import org.example.entities.SubjectDetails;

import java.util.List;

public interface SubjectDetailsDAO {
    /**
     * Read all subject details from database and put them into list.
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getAllSubjectDetails(String dbName);

    /**
     * Read subject details from database by id.
     * @param id subject details id
     * @return SubjectDetails
     */
    SubjectDetails getSubjectDetails(int id, String dbName);

    /**
     * Insert new subject details into database.
     * @param subjectDetails adding subject details
     */
    void addSubjectDetails(SubjectDetails subjectDetails, String dbName) throws Exception;

    /**
     * Update subject details data into database.
     * @param subjectDetails editing subject
     */
    void updateSubjectDetails(SubjectDetails subjectDetails, String dbName) throws Exception;

    /**
     * Delete subject details from database.
     * @param id subject details id
     */
    void deleteSubjectDetails(int id, String dbName);

    /**
     * Read subject details by teacher database and put them into list.
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getSubjectDetailsByTeacher(int id, String dbName);

    /**
     * Read subject details by class database and put them into list.
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getSubjectDetailsByPupilClass(int id, String dbName);

    /**
     * Read subject details by subject database and put them into list.
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getSubjectDetailsBySubject(int id, String dbName);

    /**
     * Get total count of subject details from database.
     * @return int
     */
    int getCountOfSubjectDetails(String dbName);

    /**
     * Get subject details list for page.
     * @param page number of page
     * @param range amount of subject details per page
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getSubjectDetailsByPage(int page, int range, String dbName);

    /**
     * Search subject details by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<SubjectDetails>
     * @throws Exception if set parameter is wrong
     */
    List<SubjectDetails> searchSubjectDetails(String val, String param, String dbName) throws Exception;

    /**
     * Read subject details by semester and teacher database and put them into list.
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getSubjectDetailsBySemesterAndTeacher(int semesterID, int teacherID, String dbName);

    /**
     * Read subject details by semester and pupil database and put them into list.
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getSubjectDetailsBySemesterAndPupil(int semesterID, int pupilID, String dbName);
}

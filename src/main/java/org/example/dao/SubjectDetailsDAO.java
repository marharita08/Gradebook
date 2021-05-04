package org.example.dao;

import org.example.entities.Subject;
import org.example.entities.SubjectDetails;

import java.util.List;

public interface SubjectDetailsDAO {
    /**
     * Read all subject details from database and put them into list.
     * @return List<SubjectDetails>
     */
    List<SubjectDetails> getAllSubjectDetails();

    /**
     * Read subject details from database by id.
     * @param id subject details id
     * @return SubjectDetails
     */
    SubjectDetails getSubjectDetails(int id);

    /**
     * Insert new subject details into database.
     * @param subjectDetails adding subject details
     */
    void addSubjectDetails(SubjectDetails subjectDetails);

    /**
     * Update subject details data into database.
     * @param subjectDetails editing subject
     */
    void updateSubjectDetails(SubjectDetails subjectDetails);

    /**
     * Delete subject details from database.
     * @param id subject details id
     */
    void deleteSubjectDetails(int id);
}

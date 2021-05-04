package org.example.dao;

import org.example.entities.PupilClass;
import org.example.entities.Subject;
import org.example.entities.SubjectDetails;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OracleSubjectDetailsDAO implements SubjectDetailsDAO{

    Connection connection = ConnectionPool.getInstance().getConnection();
    ResultSet resultSet;
    PreparedStatement preparedStatement;
    OracleSubjectDAO oracleSubjectDAO;
    OraclePupilClassDAO oraclePupilClassDAO;
    OracleTeacherDAO oracleTeacherDAO;

    public OracleSubjectDetailsDAO(OracleSubjectDAO oracleSubjectDAO,
                                   OraclePupilClassDAO oraclePupilClassDAO,
                                   OracleTeacherDAO oracleTeacherDAO) {
        this.oracleSubjectDAO = oracleSubjectDAO;
        this.oraclePupilClassDAO = oraclePupilClassDAO;
        this.oracleTeacherDAO = oracleTeacherDAO;
    }

    /**
     * Read all subject details from database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getAllSubjectDetails() {

        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    private SubjectDetails parseSubjectDetails(ResultSet resultSet) {
        SubjectDetails subjectDetails = null;
        try {
            int id = resultSet.getInt("subject_details_ID");
            int classID = resultSet.getInt("class_id");
            int teacherID = resultSet.getInt("teacher_id");
            int subjectID = resultSet.getInt("subject_id");
            PupilClass pupilClass = oraclePupilClassDAO.getPupilClass(classID);
            Subject subject = oracleSubjectDAO.getSubject(subjectID);
            if(teacherID == 0) {
                subjectDetails = new SubjectDetails(id, pupilClass, null, subject);
            } else {
                subjectDetails = new SubjectDetails(id, pupilClass, oracleTeacherDAO.getTeacher(teacherID), subject);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subjectDetails;
    }

    /**
     * Read subject details from database by id.
     * @param id subject details id
     * @return SubjectDetails
     */
    @Override
    public SubjectDetails getSubjectDetails(int id) {
        SubjectDetails subjectDetails = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS"
                            + " where SUBJECT_DETAILS_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                subjectDetails = parseSubjectDetails(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subjectDetails;
    }

    /**
     * Insert new subject details into database.
     * @param subjectDetails adding subject details
     */
    @Override
    public void addSubjectDetails(SubjectDetails subjectDetails) {
        String sql = "Insert into LAB3_ROZGHON_SUBJECT_DETAILS "
                + "values (LAB3_ROZGHON_SUBJECT_DETAILS_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, subjectDetails.getPupilClass().getId());
            if(subjectDetails.getTeacher().getId() == 0){
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(2, subjectDetails.getTeacher().getId());
            }
            preparedStatement.setInt(3, subjectDetails.getSubject().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Update subject details data into database.
     * @param subjectDetails editing subject
     */
    @Override
    public void updateSubjectDetails(SubjectDetails subjectDetails) {
        String sql = "UPDATE LAB3_ROZGHON_SUBJECT_DETAILS "
                + "set CLASS_ID = ?, TEACHER_ID = ?, SUBJECT_ID = ? where SUBJECT_DETAILS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, subjectDetails.getPupilClass().getId());
            if(subjectDetails.getTeacher().getId() == 0){
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(2, subjectDetails.getTeacher().getId());
            }
            preparedStatement.setInt(3, subjectDetails.getSubject().getId());
            preparedStatement.setInt(4, subjectDetails.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Delete subject details from database.
     * @param id subject details id
     */
    @Override
    public void deleteSubjectDetails(int id) {
        String sql = "Delete from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where SUBJECT_DETAILS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}

package org.example.dao;

import org.example.entities.PupilClass;
import org.example.entities.Subject;
import org.example.entities.SubjectDetails;
import org.example.entities.Teacher;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OracleSubjectDetailsDAO implements SubjectDetailsDAO{

    Connection connection;
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
        connection = ConnectionPool.getInstance().getConnection();
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
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
            PupilClass pupilClass;
            Subject subject;
            Teacher teacher;
            pupilClass = oraclePupilClassDAO.getPupilClass(classID);
            if (teacherID == 0) {
                teacher = null;
            } else {
                teacher = oracleTeacherDAO.getTeacher(teacherID);
            }
            subject = oracleSubjectDAO.getSubject(subjectID);
            subjectDetails = new SubjectDetails(id, pupilClass, teacher, subject);
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
        connection = ConnectionPool.getInstance().getConnection();
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
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return subjectDetails;
    }

    /**
     * Insert new subject details into database.
     * @param subjectDetails adding subject details
     */
    @Override
    public void addSubjectDetails(SubjectDetails subjectDetails) {
        connection = ConnectionPool.getInstance().getConnection();
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
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Update subject details data into database.
     * @param subjectDetails editing subject
     */
    @Override
    public void updateSubjectDetails(SubjectDetails subjectDetails) {
        connection = ConnectionPool.getInstance().getConnection();
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
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete subject details from database.
     * @param id subject details id
     */
    @Override
    public void deleteSubjectDetails(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Delete from LAB3_ROZGHON_MARK " +
                "where LESSON_ID in (SELECT LESSON_ID " +
                "from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "Delete from LAB3_ROZGHON_LESSON " +
                    "where SUBJECT_DETAILS_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "Delete from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where SUBJECT_DETAILS_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Read subject details by teacher database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsByTeacher(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                            "where TEACHER_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Read subject details by class database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsByPupilClass(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                            "where CLASS_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Read subject details by subject database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsBySubject(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                            "where SUBJECT_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Get total count of subject details from database.
     * @return int
     */
    public int getCountOfSubjectDetails() {
        connection = ConnectionPool.getInstance().getConnection();
        int count = 0;
        String sql = "select count(SUBJECT_DETAILS_ID) as AMOUNT " +
                "from LAB3_ROZGHON_SUBJECT_DETAILS ";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return count;
    }

    /**
     * Get subject details list for page.
     * @param page number of page
     * @param range amount of subject details per page
     * @return List<SubjectDetails>
     */
    public List<SubjectDetails> getSubjectDetailsByPage(int page, int range) {
        List<SubjectDetails> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS ORDER BY SUBJECT_DETAILS_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    private void closeAll(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

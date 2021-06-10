package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.PupilClass;
import org.example.entities.Subject;
import org.example.entities.SubjectDetails;
import org.example.entities.Teacher;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OracleSubjectDetailsDAO implements SubjectDetailsDAO{

    private Connection connection;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private OracleSubjectDAO oracleSubjectDAO;
    private OraclePupilClassDAO oraclePupilClassDAO;
    private OracleTeacherDAO oracleTeacherDAO;
    private static final Logger LOGGER = Logger.getLogger(OracleSubjectDetailsDAO.class.getName());

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
        LOGGER.info("Reading all subject details from database.");
        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS order by  SUBJECT_DETAILS_ID");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subject details and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            LOGGER.info("List of subject details complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    private SubjectDetails parseSubjectDetails(ResultSet resultSet) {
        SubjectDetails subjectDetails = null;
        try {
            LOGGER.info("Parsing result set into SubjectDetails.");
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
            LOGGER.info("Parsing complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading subject details " + id + " from database.");
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
            LOGGER.info("Reading complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return subjectDetails;
    }

    /**
     * Insert new subject details into database.
     * @param subjectDetails adding subject details
     */
    @Override
    public void addSubjectDetails(SubjectDetails subjectDetails) throws Exception {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Inserting subject details " + subjectDetails.getId() + " into database.");
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
            LOGGER.info("Inserting complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            Exception exception = new Exception("Class "
                    + oraclePupilClassDAO.getPupilClass(subjectDetails.getPupilClass().getId()).getName()
                    + " already has subject details for subject "
                    + oracleSubjectDAO.getSubject(subjectDetails.getSubject().getId()).getName() + ".");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Update subject details data into database.
     * @param subjectDetails editing subject
     */
    @Override
    public void updateSubjectDetails(SubjectDetails subjectDetails) throws Exception {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Updating subject details " + subjectDetails.getId() + ".");
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
            LOGGER.info("Updating complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            Exception exception = new Exception("Class "
                    + oraclePupilClassDAO.getPupilClass(subjectDetails.getPupilClass().getId()).getName()
                    + " already has subject details for subject "
                    + oracleSubjectDAO.getSubject(subjectDetails.getSubject().getId()).getName() + ".");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Delete subject details from database.
     * @param id subject details id
     */
    @Override
    public void deleteSubjectDetails(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Deleting marks for subject details " + id + ".");
        String sql = "Delete from LAB3_ROZGHON_MARK " +
                "where LESSON_ID in (SELECT LESSON_ID " +
                "from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting lessons for subject details " + id + ".");
            sql = "Delete from LAB3_ROZGHON_LESSON " +
                    "where SUBJECT_DETAILS_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting subject details " + id + ".");
            sql = "Delete from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where SUBJECT_DETAILS_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.close();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Read subject details by teacher database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsByTeacher(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading subject details for teacher " + id + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                            "where TEACHER_ID = ? order by  SUBJECT_DETAILS_ID");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subject details and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            LOGGER.info("List of subject details complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Reading subject details for class " + id + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                            "where CLASS_ID = ? order by  SUBJECT_DETAILS_ID");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subject details and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            LOGGER.info("List of subject details complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Reading subject details for subject " + id + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                            "where SUBJECT_ID = ? order by  SUBJECT_DETAILS_ID");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subject details and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            LOGGER.info("List of subject details complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    /**
     * Get total count of subject details from database.
     * @return int
     */
    @Override
    public int getCountOfSubjectDetails() {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Counting subject details.");
        int count = 0;
        String sql = "select count(SUBJECT_DETAILS_ID) as AMOUNT " +
                "from LAB3_ROZGHON_SUBJECT_DETAILS ";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return count;
    }

    /**
     * Get subject details list for page.
     * @param page number of page
     * @param range amount of subject details per page
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsByPage(int page, int range) {
        List<SubjectDetails> list = new ArrayList<>();
        LOGGER.info("Reading subject details for page " + page + ".");
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS ORDER BY SUBJECT_DETAILS_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subject details and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            LOGGER.info("List of subject details complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    /**
     * Search subject details by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<SubjectDetails>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<SubjectDetails> searchSubjectDetails(String val, String param) throws Exception {
        List<SubjectDetails> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "id":
                sql = " SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                        "where SUBJECT_DETAILS_ID like ? order by  SUBJECT_DETAILS_ID";
                break;
            case "teacher":
                sql = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                        "join LAB3_ROZGHON_TEACHER using (TEACHER_ID) " +
                        "where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
                break;
            case "class":
                sql = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                        "join LAB3_ROZGHON_CLASS using (CLASS_ID) " +
                        "where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
                break;
            case "subject":
                sql = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
                        "join LAB3_ROZGHON_SUBJECT using (SUBJECT_ID) " +
                        "where NAME like ? order by  SUBJECT_DETAILS_ID";
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        connection = ConnectionPool.getInstance().getConnection();
        try {
            LOGGER.info("Searching subject details by " + param + ".");
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subject details and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            LOGGER.info("List of subject details complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    private void closeAll() {
        if (resultSet != null) {
            try {
                LOGGER.info("Closing result set.");
                resultSet.close();
                LOGGER.info("Result set closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (preparedStatement != null) {
            try {
                LOGGER.info("Closing statement.");
                preparedStatement.close();
                LOGGER.info("Statement closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (connection != null) {
            try {
                LOGGER.info("Closing connection.");
                connection.close();
                LOGGER.info("Connection closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}

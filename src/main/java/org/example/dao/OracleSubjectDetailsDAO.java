package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.PupilClass;
import org.example.entities.Subject;
import org.example.entities.SubjectDetails;
import org.example.entities.Teacher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class OracleSubjectDetailsDAO implements SubjectDetailsDAO{

    private static final String GET_ALL_SUBJECT_DETAILS = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS order by  SUBJECT_DETAILS_ID";
    private static final String GET_SUBJECT_DETAILS = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS where SUBJECT_DETAILS_ID = ?";
    private static final String INSERT_SUBJECT_DETAILS = "Insert into LAB3_ROZGHON_SUBJECT_DETAILS values (LAB3_ROZGHON_SUBJECT_DETAILS_SEQ.nextval, ?, ?, ?)";
    private static final String UPDATE_SUBJECT_DETAILS = "UPDATE LAB3_ROZGHON_SUBJECT_DETAILS set CLASS_ID = ?, TEACHER_ID = ?, SUBJECT_ID = ? where SUBJECT_DETAILS_ID = ?";
    private static final String DELETE_MARKS_FOR_DELETING_SUBJECT_DETAILS = "Delete from LAB3_ROZGHON_MARK " +
            "where LESSON_ID in (SELECT LESSON_ID from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ?)";
    private static final String DELETE_LESSONS_FOR_DELETING_SUBJECT_DETAILS = "Delete from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ?";
    private static final String DELETE_SUBJECT_DETAILS = "Delete from LAB3_ROZGHON_SUBJECT_DETAILS where SUBJECT_DETAILS_ID = ?";
    private static final String GET_SUBJECT_DETAILS_BY_SUBJECT = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS where SUBJECT_ID = ? order by  SUBJECT_DETAILS_ID";
    private static final String GET_SUBJECT_DETAILS_BY_TEACHER = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS where TEACHER_ID = ? order by  SUBJECT_DETAILS_ID";
    private static final String GET_SUBJECT_DETAILS_BY_CLASS = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS where CLASS_ID = ? order by  SUBJECT_DETAILS_ID";
    private static final String GET_COUNT_OF_SUBJECT_DETAILS = "select count(SUBJECT_DETAILS_ID) as AMOUNT from LAB3_ROZGHON_SUBJECT_DETAILS ";
    private static final String GET_SUBJECT_DETAILS_BY_PAGE = "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
            " (SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS ORDER BY SUBJECT_DETAILS_ID) p) WHERE rn BETWEEN ? AND ?";
    private static final String SEARCH_SUBJECT_DETAILS_BY_SUBJECT = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
            "join LAB3_ROZGHON_SUBJECT using (SUBJECT_ID) where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
    private static final String SEARCH_SUBJECT_DETAILS_BY_TEACHER = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
            "join LAB3_ROZGHON_TEACHER using (TEACHER_ID) where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
    private static final String SEARCH_SUBJECT_DETAILS_BY_CLASS = "SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS " +
            "join LAB3_ROZGHON_CLASS using (CLASS_ID) where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
    private static final String SEARCH_SUBJECT_DETAILS_BY_ID = " SELECT * FROM LAB3_ROZGHON_SUBJECT_DETAILS where SUBJECT_DETAILS_ID like ? order by  SUBJECT_DETAILS_ID";
    private final OracleSubjectDAO oracleSubjectDAO;
    private final OraclePupilClassDAO oraclePupilClassDAO;
    private final OracleTeacherDAO oracleTeacherDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(OracleSubjectDetailsDAO.class.getName());

    public OracleSubjectDetailsDAO(OracleSubjectDAO oracleSubjectDAO,
                                   OraclePupilClassDAO oraclePupilClassDAO,
                                   OracleTeacherDAO oracleTeacherDAO, ConnectionPool connectionPool) {
        this.oracleSubjectDAO = oracleSubjectDAO;
        this.oraclePupilClassDAO = oraclePupilClassDAO;
        this.oracleTeacherDAO = oracleTeacherDAO;
        this.connectionPool = connectionPool;
    }

    /**
     * Read all subject details from database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getAllSubjectDetails() {
        LOGGER.info("Reading all subject details from database.");
        List<SubjectDetails> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_SUBJECT_DETAILS)) {
            while (resultSet.next()) {
                list.add(parseSubjectDetails(resultSet));
            }
            LOGGER.info("List of subject details complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading subject details " + id + " from database.");
        SubjectDetails subjectDetails = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT_DETAILS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    subjectDetails = parseSubjectDetails(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return subjectDetails;
    }

    /**
     * Insert new subject details into database.
     * @param subjectDetails adding subject details
     */
    @Override
    public void addSubjectDetails(SubjectDetails subjectDetails) throws Exception {
        LOGGER.info("Inserting subject details " + subjectDetails.getId() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SUBJECT_DETAILS)) {
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
        }
    }

    /**
     * Update subject details data into database.
     * @param subjectDetails editing subject
     */
    @Override
    public void updateSubjectDetails(SubjectDetails subjectDetails) throws Exception {
        LOGGER.info("Updating subject details " + subjectDetails.getId() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SUBJECT_DETAILS)) {
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
        }
    }

    /**
     * Delete subject details from database.
     * @param id subject details id
     */
    @Override
    public void deleteSubjectDetails(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SUBJECT_DETAILS)) {
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_MARKS_FOR_DELETING_SUBJECT_DETAILS)) {
                LOGGER.info("Deleting marks for subject details " + id + ".");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_LESSONS_FOR_DELETING_SUBJECT_DETAILS)) {
                LOGGER.info("Deleting lessons for subject details " + id + ".");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            LOGGER.info("Deleting subject details " + id + ".");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Read subject details by teacher database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsByTeacher(int id) {
        LOGGER.info("Reading subject details for teacher " + id + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT_DETAILS_BY_TEACHER)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSubjectDetails(resultSet));
                }
                LOGGER.info("List of subject details complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Read subject details by class database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsByPupilClass(int id) {
        LOGGER.info("Reading subject details for class " + id + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT_DETAILS_BY_CLASS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSubjectDetails(resultSet));
                }
                LOGGER.info("List of subject details complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Read subject details by subject database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsBySubject(int id) {

        LOGGER.info("Reading subject details for subject " + id + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT_DETAILS_BY_SUBJECT)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSubjectDetails(resultSet));
                }
                LOGGER.info("List of subject details complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Get total count of subject details from database.
     * @return int
     */
    @Override
    public int getCountOfSubjectDetails() {
        LOGGER.info("Counting subject details.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_SUBJECT_DETAILS)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT_DETAILS_BY_PAGE)) {
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSubjectDetails(resultSet));
                }
                LOGGER.info("List of subject details complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
                sql = SEARCH_SUBJECT_DETAILS_BY_ID;
                break;
            case "teacher":
                sql = SEARCH_SUBJECT_DETAILS_BY_TEACHER;
                break;
            case "class":
                sql = SEARCH_SUBJECT_DETAILS_BY_CLASS;
                break;
            case "subject":
                sql = SEARCH_SUBJECT_DETAILS_BY_SUBJECT;
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching subject details by " + param + ".");
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSubjectDetails(resultSet));
                }
                LOGGER.info("List of subject details complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

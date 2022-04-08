package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.*;
import org.example.entities.*;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostgresSubjectDetailsDAO implements SubjectDetailsDAO {

    private static final String GET_ALL_SUBJECT_DETAILS = "SELECT * FROM SUBJECT_DETAILS order by  SUBJECT_DETAILS_ID";
    private static final String GET_SUBJECT_DETAILS = "SELECT * FROM SUBJECT_DETAILS where SUBJECT_DETAILS_ID = ?";
    private static final String INSERT_SUBJECT_DETAILS = "Insert into SUBJECT_DETAILS (CLASS_ID, TEACHER_ID, SUBJECT_ID, SEMESTER_ID) values ( ?, ?, ?, ?)";
    private static final String UPDATE_SUBJECT_DETAILS = "UPDATE SUBJECT_DETAILS set CLASS_ID = ?, TEACHER_ID = ?, SUBJECT_ID = ?, SEMESTER_ID = ? where SUBJECT_DETAILS_ID = ?";
    private static final String DELETE_SUBJECT_DETAILS = "Delete from SUBJECT_DETAILS where SUBJECT_DETAILS_ID = ?";
    private static final String GET_SUBJECT_DETAILS_BY_SUBJECT = "SELECT * FROM SUBJECT_DETAILS " +
            " join SEMESTER on SEMESTER.SEMESTER_ID=SUBJECT_DETAILS.SEMESTER_ID " +
            " and SYSDATE between START_DATE and END_DATE " +
            " where SUBJECT_ID = ? order by  SUBJECT_DETAILS_ID";
    private static final String GET_SUBJECT_DETAILS_BY_TEACHER = "SELECT * FROM SUBJECT_DETAILS " +
            " join SEMESTER on SEMESTER.SEMESTER_ID=SUBJECT_DETAILS.SEMESTER_ID " +
            " and SYSDATE between START_DATE and END_DATE " +
            "where TEACHER_ID = ? order by  SUBJECT_DETAILS_ID";
    private static final String GET_SUBJECT_DETAILS_BY_CLASS = "SELECT * FROM SUBJECT_DETAILS " +
            " join SEMESTER on SEMESTER.SEMESTER_ID=SUBJECT_DETAILS.SEMESTER_ID " +
            " and SYSDATE between START_DATE and END_DATE " +
            "where CLASS_ID = ? order by  SUBJECT_DETAILS_ID";
    private static final String GET_SUBJECT_DETAILS_BY_SEMESTER_AND_TEACHER = "SELECT * from SUBJECT_DETAILS " +
            " where semester_id = ? and teacher_id = ?";
    private static final String GET_SUBJECT_DETAILS_BY_SEMESTER_AND_PUPIL = "SELECT * from SUBJECT_DETAILS sd " +
            " join pupil p on p.class_id=sd.class_id" +
            " where semester_id = ? and pupil_id = ?";
    private static final String GET_COUNT_OF_SUBJECT_DETAILS = "select count(SUBJECT_DETAILS_ID) as AMOUNT from SUBJECT_DETAILS ";
    private static final String GET_SUBJECT_DETAILS_BY_PAGE = "SELECT * FROM SUBJECT_DETAILS ORDER BY SUBJECT_DETAILS_ID limit ? offset ?";
    private static final String SEARCH_SUBJECT_DETAILS_BY_SUBJECT = "SELECT * FROM SUBJECT_DETAILS " +
            "join SUBJECT using (SUBJECT_ID) where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
    private static final String SEARCH_SUBJECT_DETAILS_BY_TEACHER = "SELECT * FROM SUBJECT_DETAILS " +
            "join TEACHER using (TEACHER_ID) where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
    private static final String SEARCH_SUBJECT_DETAILS_BY_CLASS = "SELECT * FROM SUBJECT_DETAILS " +
            "join CLASS using (CLASS_ID) where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
    private static final String SEARCH_SUBJECT_DETAILS_BY_SEMESTER = "SELECT * FROM SUBJECT_DETAILS " +
            "join SEMESTER using (SEMESTER_ID) where upper(NAME) like ? order by  SUBJECT_DETAILS_ID";
    private static final String SEARCH_SUBJECT_DETAILS_BY_ID = " SELECT * FROM SUBJECT_DETAILS where SUBJECT_DETAILS_ID like ? order by  SUBJECT_DETAILS_ID";
    private final SubjectDAO subjectDAO;
    private final PupilClassDAO pupilClassDAO;
    private final TeacherDAO teacherDAO;
    private final SemesterDAO semesterDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(PostgresSubjectDetailsDAO.class.getName());

    public PostgresSubjectDetailsDAO(PostgresSubjectDAO subjectDAO,
                                     PostgresPupilClassDAO pupilClassDAO,
                                     PostgresTeacherDAO teacherDAO, SemesterDAO semesterDAO, ConnectionPool connectionPool) {
        this.subjectDAO = subjectDAO;
        this.pupilClassDAO = pupilClassDAO;
        this.teacherDAO = teacherDAO;
        this.semesterDAO = semesterDAO;
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
            int semesterID = resultSet.getInt("semester_id");
            Teacher teacher;
            PupilClass pupilClass = pupilClassDAO.getPupilClass(classID);
            if (teacherID == 0) {
                teacher = null;
            } else {
                teacher = teacherDAO.getTeacher(teacherID);
            }
            Subject subject = subjectDAO.getSubject(subjectID);
            Semester semester = semesterDAO.getSemester(semesterID);
            subjectDetails = new SubjectDetails(id, pupilClass, teacher, subject, semester);
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
            preparedStatement.setInt(4, subjectDetails.getSemester().getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            Exception exception = new Exception("Class "
                    + pupilClassDAO.getPupilClass(subjectDetails.getPupilClass().getId()).getName()
                    + " already has subject details for subject "
                    + subjectDAO.getSubject(subjectDetails.getSubject().getId()).getName() + ".");
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
            preparedStatement.setInt(4, subjectDetails.getSemester().getId());
            preparedStatement.setInt(5, subjectDetails.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            Exception exception = new Exception("Class "
                    + pupilClassDAO.getPupilClass(subjectDetails.getPupilClass().getId()).getName()
                    + " already has subject details for subject "
                    + subjectDAO.getSubject(subjectDetails.getSubject().getId()).getName() + ".");
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
    @Transactional
    @Override
    public void deleteSubjectDetails(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SUBJECT_DETAILS)) {
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
     * Read subject details by semester and teacher database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsBySemesterAndTeacher(int semesterID, int teacherID) {
        LOGGER.info("Reading subject details for semester " + semesterID + " and teacher " + teacherID + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT_DETAILS_BY_SEMESTER_AND_TEACHER)) {
            preparedStatement.setInt(1, semesterID);
            preparedStatement.setInt(2, teacherID);
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
     * Read subject details by semester and pupil database and put them into list.
     * @return List<SubjectDetails>
     */
    @Override
    public List<SubjectDetails> getSubjectDetailsBySemesterAndPupil(int semesterID, int pupilID) {
        LOGGER.info("Reading subject details for semester " + semesterID + " and pupil " + pupilID + ".");
        List<SubjectDetails> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT_DETAILS_BY_SEMESTER_AND_PUPIL)) {
            preparedStatement.setInt(1, semesterID);
            preparedStatement.setInt(2, pupilID);
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
            preparedStatement.setInt(1, range);
            preparedStatement.setInt(2, (page - 1) * range);
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
            case "semester":
                sql = SEARCH_SUBJECT_DETAILS_BY_SEMESTER;
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

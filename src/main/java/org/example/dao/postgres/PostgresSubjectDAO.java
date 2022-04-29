package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.SubjectDAO;
import org.example.entities.Subject;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostgresSubjectDAO implements SubjectDAO {
    private final static String GET_ALL_SUBJECTS = "SELECT * FROM SUBJECT order by SUBJECT_ID";
    private final static String GET_SUBJECT = "SELECT * FROM SUBJECT where SUBJECT_ID=?";
    private final static String INSERT_SUBJECT = "Insert into SUBJECT (name) values ( ?)";
    private final static String UPDATE_SUBJECT = "UPDATE SUBJECT set NAME = ? where SUBJECT_ID = ?";
    private final static String DELETE_SUBJECT = "Delete from SUBJECT where SUBJECT_ID = ?";
    private final static String GET_SUBJECTS_BY_CLASS = "select * from SUBJECT " +
            "join SUBJECT_DETAILS using(subject_id) where CLASS_ID = ? order by SUBJECT_ID";
    private final static String GET_SUBJECTS_BY_TEACHER = "select distinct SUBJECT_ID, NAME from SUBJECT " +
            "join SUBJECT_DETAILS using(subject_id) where TEACHER_ID = ? order by SUBJECT_ID";
    private final static String GET_COUNT_OF_SUBJECTS = "select count(SUBJECT_ID) as AMOUNT from SUBJECT ";
    private final static String GET_SUBJECTS_BY_PAGE = "SELECT * FROM SUBJECT ORDER BY SUBJECT_ID limit ? offset ?";
    private final static String SEARCH_SUBJECT_BY_ID = " SELECT * FROM SUBJECT where to_char(subject_id, '99999') like ? order by SUBJECT_ID";
    private final static String SEARCH_SUBJECT_BY_NAME = " SELECT * FROM SUBJECT where upper(name) like ? order by SUBJECT_ID";
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(PostgresSubjectDAO.class.getName());

    public PostgresSubjectDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Read all subjects from database and put them into list.
     * @return List<Subject>
     */
    @Override
    public List<Subject> getAllSubjects() {
        LOGGER.info("Reading all subjects from database.");
        List<Subject> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_SUBJECTS)) {
            while (resultSet.next()) {
                list.add(parseSubject(resultSet));
            }
            LOGGER.info("List of subjects complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    private Subject parseSubject(ResultSet resultSet) {
        Subject subject = null;
        try {
            int id = resultSet.getInt("subject_ID");
            String name = resultSet.getString("NAME");
            subject = new Subject(id, name);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return subject;
    }

    /**
     * Read subject from database by id.
     * @param id subject id
     * @return Subject
     */
    @Override
    public Subject getSubject(int id) {
        LOGGER.info("Reading subject " + id + " from database.");
        Subject subject = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECT)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    subject = parseSubject(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return subject;
    }

    /**
     * Insert new subject into database.
     * @param subject adding subject
     */
    @Override
    public void addSubject(Subject subject) {
        LOGGER.info("Inserting subject  " + subject.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SUBJECT)) {
            preparedStatement.setString(1, subject.getName());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update subject data into database.
     * @param subject editing subject
     */
    @Override
    public void updateSubject(Subject subject) {
        LOGGER.info("Updating subject " + subject.getName() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SUBJECT)) {
            preparedStatement.setString(1, subject.getName());
            preparedStatement.setInt(2, subject.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete subject from database.
     * @param id subject id
     */
    @Transactional
    @Override
    public void deleteSubject(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SUBJECT)) {
            LOGGER.info("Deleting subject " + id + ".");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get subjects that are learned by class with set id.
     * @param id class id
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectsByPupilClass(int id) {
        LOGGER.info("Reading subjects for class " + id + ".");
        List<Subject> subjectList = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECTS_BY_CLASS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    subjectList.add(parseSubject(resultSet));
                }
                LOGGER.info("List of subjects complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return subjectList;
    }

    /**
     * Get subjects which are teached by teacher with set id
     * @param id teacher id
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectsByTeacher(int id) {
        LOGGER.info("Reading subject details for teacher " + id + ".");
        List<Subject> subjectList = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECTS_BY_TEACHER)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    subjectList.add(parseSubject(resultSet));
                }
                LOGGER.info("List of subjects complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return subjectList;
    }

    /**
     * Get total count of subject from database.
     * @return int
     */
    @Override
    public int getCountOfSubjects() {
        LOGGER.info("Counting subjects.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_SUBJECTS)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return count;
    }

    /**
     * Get subject list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectsByPage(int page, int range) {
        LOGGER.info("Reading subjects for page " + page + ".");
        List<Subject> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBJECTS_BY_PAGE)) {
            preparedStatement.setInt(1, range);
            preparedStatement.setInt(2, (page - 1) * range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                LOGGER.info("Parsing subjects and put them into list.");
                while (resultSet.next()) {
                    list.add(parseSubject(resultSet));
                }
                LOGGER.info("List of subjects complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Search subjects by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Subject>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<Subject> searchSubjects(String val, String param) throws Exception {
        System.out.println(val + " " + param);
        List<Subject> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        if (param.equals("name")) {
            sql = SEARCH_SUBJECT_BY_NAME;
        } else if (param.equals("id")) {
            sql = SEARCH_SUBJECT_BY_ID;
        }  else {
            Exception e = new Exception("Wrong parameter");
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        LOGGER.info("Searching subjects by " + param + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSubject(resultSet));
                }
                LOGGER.info("List of subjects complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

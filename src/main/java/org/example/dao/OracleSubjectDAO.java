package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Subject;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OracleSubjectDAO implements SubjectDAO {
    private Connection connection;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private static final Logger LOGGER = Logger.getLogger(OracleSubjectDAO.class.getName());

    /**
     * Read all subjects from database and put them into list.
     * @return List<Subject>
     */
    @Override
    public List<Subject> getAllSubjects() {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading all subjects from database.");
        List<Subject> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT order by SUBJECT_ID");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subjects and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubject(resultSet));
            }
            LOGGER.info("List of subjects complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    private Subject parseSubject(ResultSet resultSet) {
        Subject subject = null;
        try {
            LOGGER.info("Parsing result set into Subject.");
            int id = resultSet.getInt("subject_ID");
            String name = resultSet.getString("NAME");
            subject = new Subject(id, name);
            LOGGER.info("Parsing complete.");
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
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading subject " + id + " from database.");
        Subject subject = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT"
                            + " where SUBJECT_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                subject = parseSubject(resultSet);
            }
            LOGGER.info("Reading complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return subject;
    }

    /**
     * Insert new subject into database.
     * @param subject adding subject
     */
    @Override
    public void addSubject(Subject subject) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Inserting subject  " + subject.getName() + " into database.");
        String sql = "Insert into LAB3_ROZGHON_SUBJECT "
                + "values (LAB3_ROZGHON_SUBJECT_SEQ.nextval, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subject.getName());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Update subject data into database.
     * @param subject editing subject
     */
    @Override
    public void updateSubject(Subject subject) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Updating subject " + subject.getName() + ".");
        String sql = "UPDATE LAB3_ROZGHON_SUBJECT "
                + "set NAME = ? where SUBJECT_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subject.getName());
            preparedStatement.setInt(2, subject.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Delete subject from database.
     * @param id subject id
     */
    @Override
    public void deleteSubject(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Deleting marks for subject " + id);
        String sql = "Delete from LAB3_ROZGHON_MARK " +
                "where LESSON_ID in (select LESSON_ID " +
                "from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID in (" +
                "select SUBJECT_DETAILS_ID from LAB3_ROZGHON_SUBJECT_DETAILS" +
                " where SUBJECT_ID = ?))";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting lessons for subject " + id + ".");
            sql = "Delete from LAB3_ROZGHON_LESSON " +
                    "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID " +
                    "from LAB3_ROZGHON_SUBJECT_DETAILS where SUBJECT_ID = ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting subject details for subject " + id + ".");
            sql = "delete  from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where SUBJECT_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting subject " + id + ".");
            sql = "Delete from LAB3_ROZGHON_SUBJECT "
                + "where SUBJECT_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Get subjects that are learned by class with set id.
     * @param id class id
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectByPupilClass(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading subjects for class " + id + ".");
        List<Subject> subjectList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "select * from LAB3_ROZGHON_SUBJECT " +
                    "join LAB3_ROZGHON_SUBJECT_DETAILS using(subject_id)" +
                    "where CLASS_ID = ? order by SUBJECT_ID");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subjects and put them into list.");
            while (resultSet.next()) {
                subjectList.add(parseSubject(resultSet));
            }
            LOGGER.info("List of subjects complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return subjectList;
    }

    /**
     * Get subjects which are teached by teacher with set id
     * @param id teacher id
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectByTeacher(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading subject details for teacher " + id + ".");
        List<Subject> subjectList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "select distinct SUBJECT_ID, NAME " +
                    "from LAB3_ROZGHON_SUBJECT " +
                    "join LAB3_ROZGHON_SUBJECT_DETAILS using(subject_id)" +
                    "where TEACHER_ID = ? order by SUBJECT_ID");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subjects and put them into list.");
            while (resultSet.next()) {
                subjectList.add(parseSubject(resultSet));
            }
            LOGGER.info("List of subjects complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return subjectList;
    }

    /**
     * Get total count of subject from database.
     * @return int
     */
    @Override
    public int getCountOfSubjects() {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Counting subjects.");
        int count = 0;
        String sql = "select count(SUBJECT_ID) as AMOUNT " +
                "from LAB3_ROZGHON_SUBJECT ";
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
     * Get subject list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectsByPage(int page, int range) {
        List<Subject> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading subjects for page " + page + ".");
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_SUBJECT ORDER BY SUBJECT_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subjects and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubject(resultSet));
            }
            LOGGER.info("List of subjects complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        List<Subject> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        if (param.equals("name")) {
            sql = " SELECT * FROM LAB3_ROZGHON_SUBJECT " +
                    "where upper(name) like ? order by SUBJECT_ID";
        } else if (param.equals("id")) {
            sql = " SELECT * FROM LAB3_ROZGHON_SUBJECT " +
                    "where subject_id like ? order by SUBJECT_ID";
        }  else {
            Exception e = new Exception("Wrong parameter");
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Searching subjects by " + param + ".");
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing subjects and put them into list.");
            while (resultSet.next()) {
                list.add(parseSubject(resultSet));
            }
            LOGGER.info("List of subjects complete.");
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

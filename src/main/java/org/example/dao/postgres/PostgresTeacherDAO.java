package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.TeacherDAO;
import org.example.entities.Teacher;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostgresTeacherDAO implements TeacherDAO {
    private static final String GET_ALL_TEACHERS = "SELECT * FROM TEACHER order by teacher_id";
    private static final String GET_TEACHER = "SELECT * FROM TEACHER where teacher_id = ?";
    private static final String INSERT_TEACHER = "Insert into TEACHER(teacher_id, name, position) values (?, ?, ?)";
    private static final String UPDATE_TEACHER = "UPDATE TEACHER set name = ?, position = ? where teacher_id = ?";
    private static final String UPDATE_SUBJECT_DETAILS_OF_DELETING_TEACHER = "update SUBJECT_DETAILS set TEACHER_ID = null where TEACHER_ID = ?";
    private static final String DELETE_TEACHER = "Delete from TEACHER where teacher_id = ?";
    private static final String GET_TEACHERS_BY_CLASS = "select distinct TEACHER_ID, NAME, POSITION " +
            "from TEACHER join SUBJECT_DETAILS using(teacher_id) where CLASS_ID = ? order by TEACHER_ID";
    private static final String GET_TEACHERS_BY_SUBJECT = "select distinct TEACHER_ID, NAME, POSITION " +
            "from TEACHER join SUBJECT_DETAILS using(teacher_id) where SUBJECT_ID = ? order by TEACHER_ID";
    private static final String GET_COUNT_OF_TEACHERS = "select count(TEACHER_ID) as AMOUNT from TEACHER ";
    private static final String GET_TEACHERS_BY_PAGE = "SELECT * FROM TEACHER ORDER BY TEACHER_ID limit ? offset ?";
    private static final String SEARCH_TEACHERS_BY_ID = " SELECT * FROM TEACHER where to_char(teacher_id, '99999') like ? order by teacher_id";
    private static final String SEARCH_TEACHERS_BY_NAME = " SELECT * FROM TEACHER where upper(name) like ? order by teacher_id";
    private static final String SEARCH_TEACHERS_BY_POSITION = " SELECT * FROM TEACHER where upper(position) like ? order by teacher_id";
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(PostgresTeacherDAO.class.getName());

    public PostgresTeacherDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Read all teachers from database and put them into list.
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getAllTeachers(String dbName) {
        LOGGER.info("Reading all teachers from database");
        List<Teacher> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection(dbName);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_TEACHERS)) {
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
            LOGGER.info("List of teachers complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    private Teacher parseTeacher(ResultSet resultSet) {
        Teacher teacher = null;
        try {
            int id = resultSet.getInt("TEACHER_ID");
            String name = resultSet.getString("NAME");
            String position = resultSet.getString("POSITION");
            teacher = new Teacher(id, name, position);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return teacher;
    }

    /**
     * Read teacher from database by id.
     * @param id teacher's id
     * @return Teacher
     */
    @Override
    public Teacher getTeacher(int id, String dbName) {
        LOGGER.info("Reading teacher " + id + " from database.");
        Teacher teacher = null;
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TEACHER)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    teacher = parseTeacher(resultSet);
                }
                LOGGER.info("Reading complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return teacher;
    }

    /**
     * Insert new teacher into database.
     * @param teacher adding teacher
     */
    @Override
    public void addTeacher(Teacher teacher, String dbName) {
        LOGGER.info("Inserting teacher " + teacher.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TEACHER)) {
            preparedStatement.setInt(1, teacher.getId());
            preparedStatement.setString(2, teacher.getName());
            preparedStatement.setString(3, teacher.getPosition());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update teacher's data into database.
     * @param teacher editing teacher
     */
    @Override
    public void updateTeacher(Teacher teacher, String dbName) {
        LOGGER.info("Updating teacher " + teacher.getName() + ".");
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEACHER)) {
            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setString(2, teacher.getPosition());
            preparedStatement.setInt(3, teacher.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete teacher from database.
     * @param id teacher's id
     */
    @Transactional
    @Override
    public void deleteTeacher(int id, String dbName) {
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEACHER)) {
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(UPDATE_SUBJECT_DETAILS_OF_DELETING_TEACHER)) {
                LOGGER.info("Setting teacher_id=null in subject details where teacher_id was " + id + ".");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            LOGGER.info("Deleting teacher " + id + "from database.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get total count of teachers from database.
     * @return int
     */
    @Override
    public int getCountOfTeachers(String dbName) {
        LOGGER.info("Counting teachers.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection(dbName);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_TEACHERS)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return count;
    }

    /**
     * Get teacher list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getTeachersByPage(int page, int range, String dbName) {
        List<Teacher> list = new ArrayList<>();
        LOGGER.info("Reading teachers for " + page + " page.");
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TEACHERS_BY_PAGE)) {
            preparedStatement.setInt(1, range);
            preparedStatement.setInt(2, (page - 1) * range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseTeacher(resultSet));
                }
                LOGGER.info("List of teachers complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Search teachers by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Teacher>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<Teacher> searchTeachers(String val, String param, String dbName) throws Exception {
        List<Teacher> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "name":
                sql = SEARCH_TEACHERS_BY_NAME;
                break;
            case "id":
                sql = SEARCH_TEACHERS_BY_ID;
                break;
            case "position":
                sql = SEARCH_TEACHERS_BY_POSITION;
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching teachers by " + param + ".");
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseTeacher(resultSet));
                }
                LOGGER.info("List of teachers complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

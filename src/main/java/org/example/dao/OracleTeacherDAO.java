package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Teacher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class OracleTeacherDAO implements TeacherDAO  {
    private static final String GET_ALL_TEACHERS = "SELECT * FROM LAB3_ROZGHON_TEACHER order by teacher_id";
    private static final String GET_TEACHER = "SELECT * FROM LAB3_ROZGHON_TEACHER where teacher_id = ?";
    private static final String INSERT_TEACHER = "Insert into LAB3_ROZGHON_TEACHER values (LAB3_ROZGHON_TEACHER_SEQ.nextval, ?, ?, ?)";
    private static final String UPDATE_TEACHER = "UPDATE LAB3_ROZGHON_TEACHER set name = ?, position = ?, chief = ? where teacher_id = ?";
    private static final String UPDATE_SUBORDINATE_OF_DELETING_TEACHER = "update LAB3_ROZGHON_TEACHER set CHIEF = null where CHIEF = ?";
    private static final String UPDATE_SUBJECT_DETAILS_OF_DELETING_TEACHER = "update LAB3_ROZGHON_SUBJECT_DETAILS set TEACHER_ID = null where TEACHER_ID = ?";
    private static final String DELETE_TEACHER = "Delete from LAB3_ROZGHON_TEACHER where teacher_id = ?";
    private static final String GET_ENABLE_CHIEFS = "select * from (select * from LAB3_ROZGHON_TEACHER minus " +
            "select * from LAB3_ROZGHON_TEACHER start with TEACHER_ID = ? connect by nocycle prior TEACHER_ID=CHIEF) order by teacher_id";
    private static final String GET_TEACHERS_BY_CLASS = "select distinct TEACHER_ID, NAME, POSITION, CHIEF " +
            "from LAB3_ROZGHON_TEACHER join LAB3_ROZGHON_SUBJECT_DETAILS using(teacher_id) where CLASS_ID = ? order by TEACHER_ID";
    private static final String GET_TEACHERS_BY_SUBJECT = "select distinct TEACHER_ID, NAME, POSITION, CHIEF " +
            "from LAB3_ROZGHON_TEACHER join LAB3_ROZGHON_SUBJECT_DETAILS using(teacher_id) where SUBJECT_ID = ? order by TEACHER_ID";
    private static final String GET_COUNT_OF_TEACHERS = "select count(TEACHER_ID) as AMOUNT from LAB3_ROZGHON_TEACHER ";
    private static final String GET_TEACHERS_BY_PAGE = "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
            " (SELECT * FROM LAB3_ROZGHON_TEACHER ORDER BY TEACHER_ID) p) WHERE rn BETWEEN ? AND ?";
    private static final String SEARCH_TEACHERS_BY_ID = " SELECT * FROM LAB3_ROZGHON_TEACHER where teacher_id like ? order by teacher_id";
    private static final String SEARCH_TEACHERS_BY_NAME = " SELECT * FROM LAB3_ROZGHON_TEACHER where upper(name) like ? order by teacher_id";
    private static final String SEARCH_TEACHERS_BY_POSITION = " SELECT * FROM LAB3_ROZGHON_TEACHER where upper(position) like ? order by teacher_id";
    private static final String SEARCH_TEACHERS_BY_CHIEF = " select t.* from LAB3_ROZGHON_TEACHER t " +
            "join LAB3_ROZGHON_TEACHER ch on ch.TEACHER_ID=t.CHIEF where upper(ch.NAME) like ? order by t.teacher_id";
    private static final String SEARCH_TEACHERS_WITHOUT_CHIEF = "SELECT * from LAB3_ROZGHON_TEACHER where chief is null";
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(OracleTeacherDAO.class.getName());

    public OracleTeacherDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Read all teachers from database and put them into list.
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getAllTeachers() {
        LOGGER.info("Reading all teachers from database");
        List<Teacher> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
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
            int chiefID = resultSet.getInt("CHIEF");
            if (chiefID == 0) {
                teacher = new Teacher(id, name, position, null);
            } else {
                teacher = new Teacher(id, name, position, getTeacher(chiefID));
            }
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
    public Teacher getTeacher(int id) {
        LOGGER.info("Reading teacher " + id + " from database.");
        Teacher teacher = null;
        try (Connection connection = connectionPool.getConnection();
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
    public void addTeacher(Teacher teacher) {
        LOGGER.info("Inserting teacher " + teacher.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TEACHER)) {
            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setString(2, teacher.getPosition());
            if (teacher.getChief().getId() != 0) {
                preparedStatement.setInt(3, teacher.getChief().getId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
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
    public void updateTeacher(Teacher teacher) {
        LOGGER.info("Updating teacher " + teacher.getName() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEACHER)) {
            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setString(2, teacher.getPosition());
            if (teacher.getChief().getId() != 0) {
                preparedStatement.setInt(3, teacher.getChief().getId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
            preparedStatement.setInt(4, teacher.getId());
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
    @Override
    public void deleteTeacher(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEACHER)) {
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(UPDATE_SUBORDINATE_OF_DELETING_TEACHER)) {
                LOGGER.info("Setting chief=null where chief was teacher " + id + ".");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
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
     * Get list of teachers who enable to be chief to teacher with set id.
     * @param id teacher's id
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getEnableChiefs(int id) {
        LOGGER.info("Reading teachers who enable to be chief to teacher " + id + ".");
        List<Teacher> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ENABLE_CHIEFS)) {
            preparedStatement.setInt(1, id);
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
     * Get list of teachers who teach some subject in class with set id.
     * @param id class id
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getTeachersByPupilClass(int id) {
        LOGGER.info("Reading teachers who teach into " + id + " class.");
        List<Teacher> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TEACHERS_BY_CLASS)) {
            preparedStatement.setInt(1, id);
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
     * Get list of teachers who teach subject with set id.
     * @param id subject id
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getTeachersBySubject(int id) {
        LOGGER.info("Reading teachers who teach " + id + " subject.");
        List<Teacher> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TEACHERS_BY_SUBJECT)) {
            preparedStatement.setInt(1, id);
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
     * Get total count of teachers from database.
     * @return int
     */
    @Override
    public int getCountOfTeachers() {
        LOGGER.info("Counting teachers.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
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
    public List<Teacher> getTeachersByPage(int page, int range) {
        List<Teacher> list = new ArrayList<>();
        LOGGER.info("Reading teachers for " + page + " page.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TEACHERS_BY_PAGE)) {
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
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
    public List<Teacher> searchTeachers(String val, String param) throws Exception {
        List<Teacher> list = new ArrayList<>();
        if (param.equals("chief") && val.equals("-")) {
            try (Connection connection = connectionPool.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SEARCH_TEACHERS_WITHOUT_CHIEF)) {
                while (resultSet.next()) {
                    list.add(parseTeacher(resultSet));
                }
                LOGGER.info("List of teachers complete.");
            } catch (SQLException throwables) {
                LOGGER.error(throwables.getMessage(), throwables);
            }
        } else {
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
                case "chief":
                    sql = SEARCH_TEACHERS_BY_CHIEF;
                    break;
                default:
                    Exception e = new Exception("Wrong parameter");
                    LOGGER.error(e.getMessage(), e);
                    throw e;
            }
            try (Connection connection = connectionPool.getConnection();
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
        }
        return list;
    }
}

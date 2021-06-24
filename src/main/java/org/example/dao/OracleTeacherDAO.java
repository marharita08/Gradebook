package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Teacher;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OracleTeacherDAO implements TeacherDAO  {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(OracleTeacherDAO.class.getName());

    /**
     * Read all teachers from database and put them into list.
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            LOGGER.info("Reading all teachers from database");
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_TEACHER order by teacher_id");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing teachers and put them into list.");
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
            LOGGER.info("List of teachers complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    private Teacher parseTeacher(ResultSet resultSet) {
        Teacher teacher = null;
        try {
            LOGGER.info("Parsing result set into Teacher.");
            int id = resultSet.getInt("TEACHER_ID");
            String name = resultSet.getString("NAME");
            String position = resultSet.getString("POSITION");
            int chiefID = resultSet.getInt("CHIEF");
            if (chiefID == 0) {
                teacher = new Teacher(id, name, position, null);
            } else {
                teacher = new Teacher(id, name, position, getTeacher(chiefID));
            }
            LOGGER.info("Parsing complete.");
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
        Teacher teacher = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading teacher " + id + " from database.");
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_TEACHER"
                      + " where teacher_id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                teacher = parseTeacher(resultSet);
            }
            LOGGER.info("Reading complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return teacher;
    }

    /**
     * Insert new teacher into database.
     * @param teacher adding teacher
     */
    @Override
    public void addTeacher(Teacher teacher) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Inserting teacher " + teacher.getName() + " into database.");
        String sql = "Insert into LAB3_ROZGHON_TEACHER "
               + "values (LAB3_ROZGHON_TEACHER_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
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
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Update teacher's data into database.
     * @param teacher editing teacher
     */
    @Override
    public void updateTeacher(Teacher teacher) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Updating teacher " + teacher.getName() + ".");
        String sql = "UPDATE LAB3_ROZGHON_TEACHER "
               + "set name = ?, position = ?, chief = ? where teacher_id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
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
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete teacher from database.
     * @param id teacher's id
     */
    @Override
    public void deleteTeacher(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Setting chief=null where chief was teacher " + id + ".");
        String sql = "update LAB3_ROZGHON_TEACHER "
                      + "set CHIEF = null "
                    + "where CHIEF = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Setting teacher_id=null in subject details where teacher_id was " + id + ".");
            sql = "update LAB3_ROZGHON_SUBJECT_DETAILS " +
                "set TEACHER_ID = null " +
                "where TEACHER_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting teacher " + id + "from database.");
            sql = "Delete from LAB3_ROZGHON_TEACHER "
                  + "where teacher_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Get list of teachers who enable to be chief to teacher with set id.
     * @param id teacher's id
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getEnableChiefs(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading teachers who enable to be chief to teacher " + id + ".");
        List<Teacher> list = new ArrayList<>();
        String sql = "select * from (select * from LAB3_ROZGHON_TEACHER minus " +
                "select * from LAB3_ROZGHON_TEACHER" +
                " start with TEACHER_ID = ? " +
                "connect by nocycle prior TEACHER_ID=CHIEF) " +
                "order by teacher_id";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing teachers and put them into list.");
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
            LOGGER.info("List of teachers complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading teachers who teach into " + id + " class.");
        List<Teacher> list = new ArrayList<>();
        String sql = "select distinct TEACHER_ID, NAME, POSITION, CHIEF " +
                "from LAB3_ROZGHON_TEACHER " +
                "join LAB3_ROZGHON_SUBJECT_DETAILS using(teacher_id) " +
                "where CLASS_ID = ? order by TEACHER_ID";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing teachers and put them into list.");
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
            LOGGER.info("List of teachers complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading teachers who teach " + id + " subject.");
        List<Teacher> list = new ArrayList<>();
        String sql = "select distinct TEACHER_ID, NAME, POSITION, CHIEF " +
                "from LAB3_ROZGHON_TEACHER " +
                "join LAB3_ROZGHON_SUBJECT_DETAILS using(teacher_id) " +
                "where SUBJECT_ID = ? order by TEACHER_ID";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing teachers and put them into list.");
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
            LOGGER.info("List of teachers complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Get total count of teachers from database.
     * @return int
     */
    @Override
    public int getCountOfTeachers() {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Counting teachers.");
        int count = 0;
        String sql = "select count(TEACHER_ID) as AMOUNT " +
                "from LAB3_ROZGHON_TEACHER ";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading teachers for " + page + " page.");
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_TEACHER ORDER BY TEACHER_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing teachers and put them into list.");
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
            LOGGER.info("List of teachers complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "name":
                sql = " SELECT * FROM LAB3_ROZGHON_TEACHER where upper(name) like ? order by teacher_id";
                break;
            case "id":
                sql = " SELECT * FROM LAB3_ROZGHON_TEACHER where teacher_id like ? order by teacher_id";
                break;
            case "position":
                sql = " SELECT * FROM LAB3_ROZGHON_TEACHER where upper(position) like ? order by teacher_id";
                break;
            case "chief":
                sql = " select t.* " +
                        "from LAB3_ROZGHON_TEACHER t " +
                        "join LAB3_ROZGHON_TEACHER ch on ch.TEACHER_ID=t.CHIEF " +
                        "where upper(ch.NAME) like ? order by t.teacher_id";
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        connection = ConnectionPool.getInstance().getConnection();
        try {
            LOGGER.info("Searching teachers by " + param + ".");
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing teachers and put them into list.");
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
            LOGGER.info("List of teachers complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    private void closeAll(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                LOGGER.info("Closing result set.");
                resultSet.close();
                LOGGER.info("Result set closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (statement != null) {
            try {
                LOGGER.info("Closing statement.");
                statement.close();
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

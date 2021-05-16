package org.example.dao;

import org.example.entities.Mark;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OracleMarkDAO implements MarkDAO {
    Connection connection;
    ResultSet resultSet;
    PreparedStatement preparedStatement;
    OracleLessonDAO lessonDAO;
    OraclePupilDAO pupilDAO;

    public OracleMarkDAO(OracleLessonDAO lessonDAO, OraclePupilDAO pupilDAO) {
        this.lessonDAO = lessonDAO;
        this.pupilDAO = pupilDAO;
    }

    /**
     * Read all marks from database and put them into list.
     * @return List<Mark>
     */
    @Override
    public List<Mark> getAllMarks() {
        connection = ConnectionPool.getInstance().getConnection();
        List<Mark> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseMark(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    private Mark parseMark(ResultSet resultSet) {
        Mark mark = null;
        try {
            int id = resultSet.getInt("mark_ID");
            int lessonID = resultSet.getInt("lesson_id");
            int pupilID = resultSet.getInt("pupil_id");
            int markInt = resultSet.getInt("mark");
            mark = new Mark(id, pupilDAO.getPupil(pupilID), lessonDAO.getLesson(lessonID), markInt);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return mark;
    }

    /**
     * Read mark from database by id.
     * @param id mark id
     * @return Mark
     */
    @Override
    public Mark getMark(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        Mark mark = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK"
                            + " where MARK_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                mark = parseMark(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return mark;
    }

    /**
     * Insert new mark into database.
     * @param mark adding mark
     */
    @Override
    public void addMark(Mark mark) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Insert into LAB3_ROZGHON_MARK "
                + "values (LAB3_ROZGHON_MARK_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.setInt(3, mark.getMark());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Update mark data into database.
     * @param mark editing mark
     */
    @Override
    public void updateMark(Mark mark) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "UPDATE LAB3_ROZGHON_MARK "
                + "set PUPIL_ID = ?, LESSON_ID = ?, MARK = ? where MARK_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.setInt(3, mark.getMark());
            preparedStatement.setInt(4, mark.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete mark from database.
     * @param id mark id
     */
    @Override
    public void deleteMark(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Delete from LAB3_ROZGHON_MARK "
                + "where MARK_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Get marks for pupil with set id.
     * @param id pupil id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksByPupil(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Mark> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK"
                            + " where PUPIL_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseMark(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Get marks for lesson with set id.
     * @param id lesson id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksByLesson(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Mark> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK"
                            + " where LESSON_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseMark(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Get marks for subject details.
     * @param id subject details id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksBySubjectDetails(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Mark> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK"
                            + " where LESSON_ID in (" +
                            "select LESSON_ID from LAB3_ROZGHON_LESSON" +
                            " where SUBJECT_DETAILS_ID = ?)");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseMark(resultSet));
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

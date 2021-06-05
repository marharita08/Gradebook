package org.example.dao;

import org.example.entities.Lesson;
import org.example.entities.Pupil;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OracleLessonDAO implements LessonDAO {
    Connection connection;
    ResultSet resultSet;
    PreparedStatement preparedStatement;
    OracleSubjectDetailsDAO subjectDetailsDAO;

    public OracleLessonDAO(OracleSubjectDetailsDAO subjectDetailsDAO) {
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    private Lesson parseLesson(ResultSet resultSet) {
        Lesson lesson = null;
        try {
            int id = resultSet.getInt("lesson_ID");
            int subjectDetailsID = resultSet.getInt("subject_details_id");
            Date data = resultSet.getDate("lesson_date");
            String topic = resultSet.getString("topic");
            lesson = new Lesson(id, subjectDetailsDAO.getSubjectDetails(subjectDetailsID), data, topic);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lesson;
    }

    /**
     * Read lesson from database by id.
     * @param id lesson id
     * @return Lesson
     */
    @Override
    public Lesson getLesson(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        Lesson lesson = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_LESSON"
                            + " where LESSON_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lesson = parseLesson(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return lesson;
    }

    /**
     * Insert new lesson into database.
     * @param lesson adding lesson
     */
    @Override
    public void addLesson(Lesson lesson) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Insert into LAB3_ROZGHON_LESSON "
                + "values (LAB3_ROZGHON_LESSON_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Update lesson data into database.
     * @param lesson editing lesson
     */
    @Override
    public void updateLesson(Lesson lesson) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "UPDATE LAB3_ROZGHON_LESSON "
                + "set SUBJECT_DETAILS_ID = ?, LESSON_DATE = ?, TOPIC = ? where LESSON_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.setInt(4, lesson.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete lesson from database.
     * @param id lesson id
     */
    @Override
    public void deleteLesson(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Delete from LAB3_ROZGHON_MARK "
                + "where LESSON_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "Delete from LAB3_ROZGHON_LESSON "
                    + "where LESSON_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Read lesson from database by class and subject.
     * @param id subject details id
     * @return List<Lesson>
     */
    @Override
    public List<Lesson> getLessonsBySubjectDetails(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Lesson> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "select * from LAB3_ROZGHON_LESSON " +
                    "where SUBJECT_DETAILS_ID = ? " +
                            "order by LESSON_DATE");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Get count of lessons for set subject details from database.
     * @param id subject details id
     * @return int
     */
    @Override
    public int getCountOfLessons(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        int count = 0;
        String sql = "select count(LESSON_ID) as AMOUNT " +
                "from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
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
     * Get lesson list for page.
     * @param page number of page
     * @param range amount of lessons per page
     * @param id subject details id
     * @return List<Lesson>
     */
    @Override
    public List<Lesson> getLessonsBySubjectDetailsAndPage(int id, int page, int range) {
        List<Lesson> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_LESSON " +
                            "where SUBJECT_DETAILS_ID = ? order by LESSON_DATE) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, (page - 1) * range + 1);
            preparedStatement.setInt(3, page*range);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Search lessons by set parameter and subject details.
     * @param val text of searching
     * @param param parameter of searching
     * @param id subject details id
     * @return List<PupilClass>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<Lesson> searchLessons(String val, String param, int id) throws Exception {
        List<Lesson> list = new ArrayList<>();
        String sql;
        switch (param) {
            case "id":
                sql = "SELECT * FROM LAB3_ROZGHON_LESSON " +
                        "where SUBJECT_DETAILS_ID = ? and LESSON_ID like ? " +
                        "order by LESSON_DATE";
                break;
            case "date":
                sql = "SELECT * FROM LAB3_ROZGHON_LESSON " +
                        "where SUBJECT_DETAILS_ID = ? and LESSON_DATE like ? " +
                        "order by LESSON_DATE";
                break;
            case "topic":
                sql = "SELECT * FROM LAB3_ROZGHON_LESSON " +
                        "where SUBJECT_DETAILS_ID = ? and upper(TOPIC) like ? " +
                        "order by LESSON_DATE";
                break;
            default:
                throw new Exception("Wrong parameter");
        }
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
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

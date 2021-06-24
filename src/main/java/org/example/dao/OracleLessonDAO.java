package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Lesson;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OracleLessonDAO implements LessonDAO {
    private Connection connection;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private OracleSubjectDetailsDAO subjectDetailsDAO;
    private static final Logger LOGGER = Logger.getLogger(OracleLessonDAO.class.getName());

    public OracleLessonDAO(OracleSubjectDetailsDAO subjectDetailsDAO) {
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    private Lesson parseLesson(ResultSet resultSet) {
        Lesson lesson = null;
        try {
            LOGGER.info("Parsing result set into Lesson.");
            int id = resultSet.getInt("lesson_ID");
            int subjectDetailsID = resultSet.getInt("subject_details_id");
            Date data = resultSet.getDate("lesson_date");
            String topic = resultSet.getString("topic");
            lesson = new Lesson(id, subjectDetailsDAO.getSubjectDetails(subjectDetailsID), data, topic);
            LOGGER.info("Parsing complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading lesson " + id + " from database.");
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
            LOGGER.info("Reading complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Inserting lesson " + lesson.getId() + " into database.");
        String sql = "Insert into LAB3_ROZGHON_LESSON "
                + "values (LAB3_ROZGHON_LESSON_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Update lesson data into database.
     * @param lesson editing lesson
     */
    @Override
    public void updateLesson(Lesson lesson) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Updating lesson " + lesson.getId() + ".");
        String sql = "UPDATE LAB3_ROZGHON_LESSON "
                + "set SUBJECT_DETAILS_ID = ?, LESSON_DATE = ?, TOPIC = ? where LESSON_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.setInt(4, lesson.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Delete lesson from database.
     * @param id lesson id
     */
    @Override
    public void deleteLesson(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Deleting marks for " + id + " lesson.");
        String sql = "Delete from LAB3_ROZGHON_MARK "
                + "where LESSON_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting " + id + " lesson.");
            sql = "Delete from LAB3_ROZGHON_LESSON "
                    + "where LESSON_ID = ?";
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
     * Read lesson from database by class and subject.
     * @param id subject details id
     * @return List<Lesson>
     */
    @Override
    public List<Lesson> getLessonsBySubjectDetails(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading lessons for " + id + " subject details.");
        List<Lesson> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "select * from LAB3_ROZGHON_LESSON " +
                    "where SUBJECT_DETAILS_ID = ? " +
                            "order by LESSON_DATE");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing lessons and put them into list.");
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
            }
            LOGGER.info("List of lessons complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Counting lessons for " + id + " subject details.");
        int count = 0;
        String sql = "select count(LESSON_ID) as AMOUNT " +
                "from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
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
        LOGGER.info("Reading lessons for " + id + " subject details and page " + page + ".");
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
            LOGGER.info("Parsing lessons and put them into list.");
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
            }
            LOGGER.info("List of lessons complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Checking parameter of searching.");
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
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        connection = ConnectionPool.getInstance().getConnection();
        try {
            LOGGER.info("Searching lessons by " + param + ".");
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing lessons and put them into list.");
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
            }
            LOGGER.info("List of lessons complete.");
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

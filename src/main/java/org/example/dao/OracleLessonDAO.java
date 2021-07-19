package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Lesson;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class OracleLessonDAO implements LessonDAO {
    private static final String GET_LESSON = "SELECT * FROM LAB3_ROZGHON_LESSON where LESSON_ID = ?";
    private static final String INSERT_LESSON = "Insert into LAB3_ROZGHON_LESSON values (LAB3_ROZGHON_LESSON_SEQ.nextval, ?, ?, ?)";
    private static final String UPDATE_LESSON = "UPDATE LAB3_ROZGHON_LESSON set SUBJECT_DETAILS_ID = ?, LESSON_DATE = ?, TOPIC = ? where LESSON_ID = ?";
    private static final String DELETE_MARKS_FOR_LESSON = "Delete from LAB3_ROZGHON_MARK where LESSON_ID = ?";
    private static final String DELETE_LESSON = "Delete from LAB3_ROZGHON_LESSON where LESSON_ID = ?";
    private static final String GET_LESSONS_BY_SUBJECT_DETAILS = "select * from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ? order by LESSON_DATE";
    private static final String GET_LESSONS_BY_PAGE_AND_SUBJECT_DETAILS = "SELECT * FROM (SELECT p.*, ROWNUM rn FROM (SELECT * FROM LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ? order by LESSON_DATE) p) WHERE rn BETWEEN ? AND ?";
    private static final String GET_COUNT_OF_LESSONS = "select count(LESSON_ID) as AMOUNT from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ?";
    private static final String SEARCH_BY_ID = "SELECT * FROM LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ? and LESSON_ID like ? order by LESSON_DATE";
    private static final String SEARCH_BY_DATE = "SELECT * FROM LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ? and LESSON_DATE like ? order by LESSON_DATE";
    private static final String SEARCH_BY_TOPIC = "SELECT * FROM LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID = ? and upper(TOPIC) like ? order by LESSON_DATE";
    private final OracleSubjectDetailsDAO subjectDetailsDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(OracleLessonDAO.class.getName());

    public OracleLessonDAO(OracleSubjectDetailsDAO subjectDetailsDAO,
                           ConnectionPool connectionPool) {
        this.subjectDetailsDAO = subjectDetailsDAO;
        this.connectionPool = connectionPool;
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
        LOGGER.info("Reading lesson " + id + " from database.");
        Lesson lesson = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LESSON)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    lesson = parseLesson(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return lesson;
    }

    /**
     * Insert new lesson into database.
     * @param lesson adding lesson
     */
    @Override
    public void addLesson(Lesson lesson) {
        LOGGER.info("Inserting lesson " + lesson.getId() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LESSON)) {
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update lesson data into database.
     * @param lesson editing lesson
     */
    @Override
    public void updateLesson(Lesson lesson) {
        LOGGER.info("Updating lesson " + lesson.getId() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LESSON)) {
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.setInt(4, lesson.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete lesson from database.
     * @param id lesson id
     */
    @Override
    public void deleteLesson(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_LESSON)) {
            LOGGER.info("Deleting marks for " + id + " lesson.");
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_MARKS_FOR_LESSON)){
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            LOGGER.info("Deleting " + id + " lesson.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Read lesson from database by class and subject.
     * @param id subject details id
     * @return List<Lesson>
     */
    @Override
    public List<Lesson> getLessonsBySubjectDetails(int id) {
        LOGGER.info("Reading lessons for " + id + " subject details.");
        List<Lesson> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LESSONS_BY_SUBJECT_DETAILS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    list.add(parseLesson(resultSet));
                }
                LOGGER.info("List of lessons complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Counting lessons for " + id + " subject details.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_COUNT_OF_LESSONS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                count = resultSet.getInt("AMOUNT");
                LOGGER.info("Counting complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading lessons for " + id + " subject details and page " + page + ".");
        List<Lesson> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LESSONS_BY_PAGE_AND_SUBJECT_DETAILS)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, (page - 1) * range + 1);
            preparedStatement.setInt(3, page*range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseLesson(resultSet));
                }
                LOGGER.info("List of lessons complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
                sql = SEARCH_BY_ID;
                break;
            case "date":
                sql = SEARCH_BY_DATE;
                break;
            case "topic":
                sql = SEARCH_BY_TOPIC;
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching lessons by " + param + ".");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseLesson(resultSet));
                }
                LOGGER.info("List of lessons complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

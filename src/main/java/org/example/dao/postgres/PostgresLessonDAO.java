package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.LessonDAO;
import org.example.dao.interfaces.ThemeDAO;
import org.example.entities.Lesson;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresLessonDAO implements LessonDAO {
    private static final String GET_LESSON = "SELECT * FROM LESSON where LESSON_ID = ?";
    private static final String INSERT_LESSON = "Insert into LESSON (theme_id, lesson_date, topic) values (?, ?, ?)";
    private static final String UPDATE_LESSON = "UPDATE LESSON set THEME_ID = ?, LESSON_DATE = ?, TOPIC = ? where LESSON_ID = ?";
    private static final String DELETE_MARKS_FOR_LESSON = "Delete from MARK where LESSON_ID = ?";
    private static final String DELETE_LESSON = "Delete from LESSON where LESSON_ID = ?";
    private static final String GET_LESSONS_BY_THEME = "select * from LESSON where THEME_ID = ? order by LESSON_DATE";
    private final ThemeDAO themeDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(PostgresLessonDAO.class.getName());

    public PostgresLessonDAO(ThemeDAO themeDAO, ConnectionPool connectionPool) {
        this.themeDAO = themeDAO;
        this.connectionPool = connectionPool;
    }

    private Lesson parseLesson(ResultSet resultSet) {
        Lesson lesson = null;
        try {
            int id = resultSet.getInt("lesson_ID");
            int themeID = resultSet.getInt("theme_id");
            Date data = resultSet.getDate("lesson_date");
            String topic = resultSet.getString("topic");
            lesson = new Lesson(id, themeDAO.getTheme(themeID), data, topic);
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
            preparedStatement.setInt(1, lesson.getTheme().getId());
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
            preparedStatement.setInt(1, lesson.getTheme().getId());
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
    @Transactional
    @Override
    public void deleteLesson(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_LESSON)) {
            LOGGER.info("Deleting " + id + " lesson.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Read lesson from database by theme.
     * @param id theme id
     * @return List<Lesson>
     */
    @Override
    public List<Lesson> getLessonsByTheme(int id) {
        LOGGER.info("Reading lessons for " + id + " theme.");
        List<Lesson> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LESSONS_BY_THEME)) {
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
}

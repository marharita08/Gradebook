package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Lesson;
import org.example.entities.Mark;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OracleMarkDAO implements MarkDAO {
    private static final String GET_MARK = "SELECT * FROM MARK where MARK_ID = ?";
    private static final String INSERT_MARK = "Insert into MARK values (MARK_SEQ.nextval, ?, ?, ?)";
    private static final String UPDATE_MARK = "UPDATE MARK set PUPIL_ID = ?, LESSON_ID = ?, MARK = ? where MARK_ID = ?";
    private static final String DELETE_MARK = "Delete from MARK where MARK_ID = ?";
    private static final String GET_MARKS_BY_PUPIL = "SELECT * FROM MARK where PUPIL_ID = ?";
    private static final String GET_MARKS_BY_LESSON = "SELECT * FROM MARK where LESSON_ID = ?";
    private static final String GET_MARKS_BY_SUBJECT_DETAILS = "SELECT * FROM MARK where LESSON_ID in (" +
            "select LESSON_ID from LESSON where THEME_ID in (select THEME_ID from THEME where SUBJECT_DETAILS_ID = ?))";
    private final LessonDAO lessonDAO;
    private final PupilDAO pupilDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(OracleMarkDAO.class.getName());

    public OracleMarkDAO(OracleLessonDAO lessonDAO,
                         OraclePupilDAO pupilDAO,
                         ConnectionPool connectionPool) {
        this.lessonDAO = lessonDAO;
        this.pupilDAO = pupilDAO;
        this.connectionPool = connectionPool;
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
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading mark " + id + " from database.");
        Mark mark = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARK)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    mark = parseMark(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return mark;
    }

    /**
     * Insert new mark into database.
     * @param mark adding mark
     */
    @Override
    public void addMark(Mark mark) throws Exception {
        LOGGER.info("Inserting mark " + mark.getId() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MARK)) {
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.setInt(3, mark.getMark());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            throwException(mark);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update mark data into database.
     * @param mark editing mark
     */
    @Override
    public void updateMark(Mark mark) throws Exception {
        LOGGER.info("Updating mark " + mark.getId() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MARK)) {
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.setInt(3, mark.getMark());
            preparedStatement.setInt(4, mark.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            throwException(mark);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    private void throwException(Mark mark) throws Exception {
        Lesson lesson = lessonDAO.getLesson(mark.getLesson().getId());
        Exception exception = new Exception("Pupil "
                + pupilDAO.getPupil(mark.getPupil().getId()).getName()
                + " already has mark for lesson "
                + lesson.getTheme().getSubjectDetails().getSubject().getName() + " "
                + lesson.getDate() + " " + lesson.getTopic() + ".");
        LOGGER.error(exception.getMessage(), exception);
        throw exception;
    }

    /**
     * Delete mark from database.
     * @param id mark id
     */
    @Override
    public void deleteMark(int id) {
        LOGGER.info("Deleting " + id + " mark.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MARK)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get marks for pupil with set id.
     * @param id pupil id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksByPupil(int id) {
        LOGGER.info("Reading marks for " + id + " pupil.");
        List<Mark> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARKS_BY_PUPIL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    list.add(parseMark(resultSet));
                }
                LOGGER.info("List of marks complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading marks for " + id + " lesson.");
        List<Mark> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARKS_BY_LESSON)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    list.add(parseMark(resultSet));
                }
                LOGGER.info("List of marks complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading marks for " + id + " subject details.");
        List<Mark> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARKS_BY_SUBJECT_DETAILS)) {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseMark(resultSet));
                }
                LOGGER.info("List of marks complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

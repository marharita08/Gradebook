package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Lesson;
import org.example.entities.Mark;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OracleMarkDAO implements MarkDAO {
    private Connection connection;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private OracleLessonDAO lessonDAO;
    private OraclePupilDAO pupilDAO;
    private static final Logger LOGGER = Logger.getLogger(OracleMarkDAO.class.getName());

    public OracleMarkDAO(OracleLessonDAO lessonDAO, OraclePupilDAO pupilDAO) {
        this.lessonDAO = lessonDAO;
        this.pupilDAO = pupilDAO;
    }

    private Mark parseMark(ResultSet resultSet) {
        Mark mark = null;
        try {
            LOGGER.info("Parsing result set into Mark.");
            int id = resultSet.getInt("mark_ID");
            int lessonID = resultSet.getInt("lesson_id");
            int pupilID = resultSet.getInt("pupil_id");
            int markInt = resultSet.getInt("mark");
            mark = new Mark(id, pupilDAO.getPupil(pupilID), lessonDAO.getLesson(lessonID), markInt);
            LOGGER.info("Parsing complete.");
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
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading mark " + id + " from database.");
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
            LOGGER.info("Reading complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return mark;
    }

    /**
     * Insert new mark into database.
     * @param mark adding mark
     */
    @Override
    public void addMark(Mark mark) throws Exception {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Inserting mark " + mark.getId() + " into database.");
        String sql = "Insert into LAB3_ROZGHON_MARK "
                + "values (LAB3_ROZGHON_MARK_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.setInt(3, mark.getMark());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            Lesson lesson = lessonDAO.getLesson(mark.getLesson().getId());
            Exception exception = new Exception("Pupil "
                    + pupilDAO.getPupil(mark.getPupil().getId()).getName()
                    + " already has mark for lesson "
                    + lesson.getSubjectDetails().getSubject().getName() + " "
                    + lesson.getDate() + " " + lesson.getTopic() + ".");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Update mark data into database.
     * @param mark editing mark
     */
    @Override
    public void updateMark(Mark mark) throws Exception {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Updating mark " + mark.getId() + ".");
        String sql = "UPDATE LAB3_ROZGHON_MARK "
                + "set PUPIL_ID = ?, LESSON_ID = ?, MARK = ? where MARK_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.setInt(3, mark.getMark());
            preparedStatement.setInt(4, mark.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            Lesson lesson = lessonDAO.getLesson(mark.getLesson().getId());
            Exception exception = new Exception("Pupil "
                    + pupilDAO.getPupil(mark.getPupil().getId()).getName()
                    + " already has mark for lesson "
                    + lesson.getSubjectDetails().getSubject().getName() + " "
                    + lesson.getDate() + " " + lesson.getTopic() + ".");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Delete mark from database.
     * @param id mark id
     */
    @Override
    public void deleteMark(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Deleting " + id + " mark.");
        String sql = "Delete from LAB3_ROZGHON_MARK "
                + "where MARK_ID = ?";
        try {
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
     * Get marks for pupil with set id.
     * @param id pupil id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksByPupil(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading marks for " + id + " pupil.");
        List<Mark> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK"
                            + " where PUPIL_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing marks and put them into list.");
            while (resultSet.next()) {
                list.add(parseMark(resultSet));
            }
            LOGGER.info("List of marks complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Reading marks for " + id + " lesson.");
        List<Mark> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK"
                            + " where LESSON_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing marks and put them into list.");
            while (resultSet.next()) {
                list.add(parseMark(resultSet));
            }
            LOGGER.info("List of marks complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Reading marks for " + id + " subject details.");
        List<Mark> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_MARK"
                            + " where LESSON_ID in (" +
                            "select LESSON_ID from LAB3_ROZGHON_LESSON" +
                            " where SUBJECT_DETAILS_ID = ?)");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing marks and put them into list.");
            while (resultSet.next()) {
                list.add(parseMark(resultSet));
            }
            LOGGER.info("List of marks complete.");
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

package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.SubjectDetails;
import org.example.entities.Theme;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OracleThemeDAO implements ThemeDAO {
    private static final String GET_THEME = "SELECT * FROM THEME where THEME_ID=?";
    private static final String INSERT_THEME = "Insert into THEME values (THEME_SEQ.nextval, ?, ?)";
    private static final String UPDATE_THEME = "UPDATE THEME set SUBJECT_DETAILS_id = ?, name = ? where THEME_ID=?";
    private static final String DELETE_MARKS_FOR_DELETING_THEME = "Delete from MARK where LESSON_ID in" +
            " (select LESSON_ID from LESSON where THEME_id = ?)";
    private static final String DELETE_LESSONS_FOR_DELETING_THEME = "Delete from  LESSON where THEME_id = ?";
    private static final String DELETE_THEME = "Delete from THEME where THEME_id = ?";
    private static final String GET_THEMES_BY_SUBJECT_DETAILS = "select * from THEME where SUBJECT_DETAILS_ID = ? order by THEME_ID";
    private final ConnectionPool connectionPool;
    private final SubjectDetailsDAO subjectDetailsDAO;
    private static final Logger LOGGER = Logger.getLogger(OracleTeacherDAO.class.getName());

    public OracleThemeDAO(ConnectionPool connectionPool, SubjectDetailsDAO subjectDetailsDAO) {
        this.connectionPool = connectionPool;
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    private Theme parseTheme(ResultSet resultSet) {
        Theme theme = null;
        try {
            int id = resultSet.getInt("THEME_ID");
            int subjectDetailsID = resultSet.getInt("SUBJECT_DETAILS_ID");
            SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(subjectDetailsID);
            String name = resultSet.getString("NAME");
            theme = new Theme(id, subjectDetails, name);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return theme;
    }

    /**
     * Read theme from database by id.
     * @param id theme's id
     * @return Theme
     */
    @Override
    public Theme getTheme(int id) {
        LOGGER.info("Reading theme " + id + " from database.");
        Theme theme = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_THEME)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    theme = parseTheme(resultSet);
                }
                LOGGER.info("Reading complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return theme;
    }

    /**
     * Insert new theme into database.
     * @param theme adding theme
     */
    @Override
    public void addTheme(Theme theme) {
        LOGGER.info("Inserting theme " + theme.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_THEME)) {
            preparedStatement.setInt(1, theme.getSubjectDetails().getId());
            preparedStatement.setString(2, theme.getName());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update theme's data into database.
     * @param theme editing theme
     */
    @Override
    public void updateTheme(Theme theme) {
        LOGGER.info("Updating theme " + theme.getName() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_THEME)) {
            preparedStatement.setInt(1, theme.getSubjectDetails().getId());
            preparedStatement.setString(2, theme.getName());
            preparedStatement.setInt(3, theme.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete theme from database.
     * @param id theme's id
     */
    @Transactional
    @Override
    public void deleteTheme(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_THEME)) {
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_MARKS_FOR_DELETING_THEME)) {
                LOGGER.info("Deleting marks for theme " + id);
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_LESSONS_FOR_DELETING_THEME)) {
                LOGGER.info("Deleting lessons for theme " + id);
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            LOGGER.info("Deleting theme " + id + "from database.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Read themes from database by class and subject.
     * @param id subject details id
     * @return List<Theme>
     */
    @Override
    public List<Theme> getThemesBySubjectDetails(int id) {
        LOGGER.info("Reading themes for " + id + " subject details.");
        List<Theme> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_THEMES_BY_SUBJECT_DETAILS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    list.add(parseTheme(resultSet));
                }
                LOGGER.info("List of themes complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

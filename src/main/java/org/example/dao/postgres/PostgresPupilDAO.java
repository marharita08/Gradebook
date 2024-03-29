package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.PupilClassDAO;
import org.example.dao.interfaces.PupilDAO;
import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
@Repository
public class PostgresPupilDAO implements PupilDAO {
    private static final String GET_ALL_PUPILS = "SELECT * FROM PUPIL join gradebook_user on user_id=pupil_id order by PUPIL_ID";
    private static final String GET_PUPIL = "SELECT * FROM PUPIL join gradebook_user on user_id=pupil_id where PUPIL_ID=?";
    private static final String INSERT_PUPIL = "Insert into PUPIL (pupil_id, class_id, name) values (?, ?, ?)";
    private static final String UPDATE_PUPIL = "UPDATE PUPIL set CLASS_ID = ?, NAME = ? where PUPIL_ID = ?";
    private static final String DELETE_PUPIL = "Delete from PUPIL where PUPIL_ID = ?";
    private static final String GET_PUPILS_BY_CLASS = "SELECT * FROM PUPIL join gradebook_user on user_id=pupil_id where CLASS_ID = ? order by NAME";
    private static final String GET_COUNT_OF_PUPILS = "select count(PUPIL_ID) as AMOUNT from PUPIL ";
    private static final String GET_PUPILS_BY_PAGE = "SELECT * FROM PUPIL join gradebook_user on user_id=pupil_id ORDER BY PUPIL_ID limit ? offset ?";
    private static final String SEARCH_PUPIL_BY_ID = " SELECT * FROM PUPIL join gradebook_user on user_id=pupil_id where to_char(PUPIL_ID, '99999') like ? order by PUPIL_ID";
    private static final String SEARCH_PUPIL_BY_NAME = "SELECT * FROM PUPIL join gradebook_user on user_id=pupil_id where upper(NAME) like ? order by PUPIL_ID";
    private static final String SEARCH_PUPIL_BY_CLASS = "SELECT * FROM PUPIL join gradebook_user on user_id=pupil_id " +
            "join CLASS c using (CLASS_ID) where upper(c.NAME) like ? order by PUPIL_ID";
    private final PupilClassDAO pupilClassDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(PostgresPupilDAO.class.getName());

    public PostgresPupilDAO(PostgresPupilClassDAO pupilClassDAO,
                            ConnectionPool connectionPool) {
        this.pupilClassDAO = pupilClassDAO;
        this.connectionPool = connectionPool;
    }

    /**
     * Read all pupils from database and put them into list.
     * @return List<Pupil>
     */
    @Override
    public List<Pupil> getAllPupils(String dbName) {
        LOGGER.info("Reading all pupils from database.");
        List<Pupil> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection(dbName);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_PUPILS)) {
            while (resultSet.next()) {
                list.add(parsePupil(resultSet, dbName));
            }
            LOGGER.info("List of pupils complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    private Pupil parsePupil(ResultSet resultSet, String dbName) {
        Pupil pupil = null;
        try {
            int id = resultSet.getInt("Pupil_ID");
            int classID = resultSet.getInt("class_id");
            String name = resultSet.getString("NAME");
            String photo = resultSet.getString("photo");
            if (classID == 0) {
                pupil = new Pupil(id, name, null, photo);
            } else {
                PupilClass pupilClass = pupilClassDAO.getPupilClass(classID, dbName);
                pupil = new Pupil(id, name, pupilClass, photo);
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return pupil;
    }

    /**
     * Read pupil from database by id.
     * @param id pupil id
     * @return Pupil
     */
    @Override
    public Pupil getPupil(int id, String dbName) {
        LOGGER.info("Reading pupil " + id + " from database.");
        Pupil pupil = null;
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PUPIL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    pupil = parsePupil(resultSet, dbName);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return pupil;
    }

    /**
     * Insert new pupil into database.
     * @param pupil adding pupil
     */
    @Override
    public void addPupil(Pupil pupil, String dbName) {
        LOGGER.info("Inserting pupil " + pupil.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PUPIL)) {
            preparedStatement.setInt(1, pupil.getId());
            if (pupil.getPupilClass().getId() != 0) {
                preparedStatement.setInt(2, pupil.getPupilClass().getId());
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }
            preparedStatement.setString(3, pupil.getName());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update class data into database.
     * @param pupil editing pupil
     */
    @Override
    public void updatePupil(Pupil pupil, String dbName) {
        LOGGER.info("Updating pupil " + pupil.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PUPIL)) {
            if (pupil.getPupilClass().getId() != 0) {
                preparedStatement.setInt(1, pupil.getPupilClass().getId());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            preparedStatement.setString(2, pupil.getName());
            preparedStatement.setInt(3, pupil.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete pupil from database.
     * @param id pupil id
     */
    @Transactional
    @Override
    public void deletePupil(int id, String dbName) {
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PUPIL)) {
            LOGGER.info("Deleting pupil " + id + ".");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get list of pupils who study in set class.
     * @param id class id
     * @return List<Pupil>
     */
    @Override
    public List<Pupil> getPupilsByPupilClass(int id, String dbName) {
        LOGGER.info("Reading pupils for class " + id + ".");
        List<Pupil> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PUPILS_BY_CLASS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupil(resultSet, dbName));
                }
                LOGGER.info("List of pupils complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Get total count of pupils from database.
     * @return int
     */
    @Override
    public int getCountOfPupils(String dbName) {
        LOGGER.info("Counting pupils.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection(dbName);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_PUPILS)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return count;
    }

    /**
     * Get pupil list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Pupil>
     */
    @Override
    public List<Pupil> getPupilsByPage(int page, int range, String dbName) {
        LOGGER.info("Reading pupils for page " + page + ".");
        List<Pupil> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PUPILS_BY_PAGE)) {
            preparedStatement.setInt(1, range);
            preparedStatement.setInt(2, (page - 1) * range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupil(resultSet, dbName));
                }
                LOGGER.info("List of pupils complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Search pupils by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Pupil>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<Pupil> searchPupils(String val, String param, String dbName) throws Exception {
        List<Pupil> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "id":
                sql = SEARCH_PUPIL_BY_ID;
                break;
            case "name":
                sql = SEARCH_PUPIL_BY_NAME;
                break;
            case "class":
                sql = SEARCH_PUPIL_BY_CLASS;
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching pupils by " + param + ".");
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupil(resultSet, dbName));
                }
                LOGGER.info("List of pupils complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

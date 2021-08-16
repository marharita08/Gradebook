package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class OraclePupilDAO implements PupilDAO {
    private static final String GET_ALL_PUPILS = "SELECT * FROM PUPIL order by PUPIL_ID";
    private static final String GET_PUPIL = "SELECT * FROM PUPIL where PUPIL_ID=?";
    private static final String INSERT_PUPIL = "Insert into PUPIL values (USER_SEQ.nextval, ?, ?)";
    private static final String UPDATE_PUPIL = "UPDATE PUPIL set CLASS_ID = ?, NAME = ? where PUPIL_ID = ?";
    private static final String DELETE_ROLES_OF_DELETING_PUPIL = "Delete from USER_ROLE where user_id = ?";
    private static final String DELETE_USER_FOR_DELETING_PUPIL = "Delete from GRADEBOOK_USER where user_id = ?";
    private static final String DELETE_MARKS_FOR_DELETING_PUPIL = "Delete from MARK where PUPIL_ID = ?";
    private static final String DELETE_PUPIL = "Delete from PUPIL where PUPIL_ID = ?";
    private static final String GET_PUPILS_BY_CLASS = "SELECT * FROM PUPIL where CLASS_ID = ? order by NAME";
    private static final String GET_COUNT_OF_PUPILS = "select count(PUPIL_ID) as AMOUNT from PUPIL ";
    private static final String GET_PUPILS_BY_PAGE = "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
            " (SELECT * FROM PUPIL ORDER BY PUPIL_ID) p) WHERE rn BETWEEN ? AND ?";
    private static final String SEARCH_PUPIL_BY_ID = " SELECT * FROM PUPIL where PUPIL_ID like ? order by PUPIL_ID";
    private static final String SEARCH_PUPIL_BY_NAME = "SELECT * FROM PUPIL where upper(NAME) like ? order by PUPIL_ID";
    private static final String SEARCH_PUPIL_BY_CLASS = "SELECT * FROM PUPIL " +
            "join CLASS c using (CLASS_ID) where upper(c.NAME) like ? order by PUPIL_ID";
    private final PupilClassDAO pupilClassDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(OraclePupilDAO.class.getName());

    public OraclePupilDAO(OraclePupilClassDAO pupilClassDAO,
                          ConnectionPool connectionPool) {
        this.pupilClassDAO = pupilClassDAO;
        this.connectionPool = connectionPool;
    }

    /**
     * Read all pupils from database and put them into list.
     * @return List<Pupil>
     */
    @Override
    public List<Pupil> getAllPupils() {
        LOGGER.info("Reading all pupils from database.");
        List<Pupil> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_PUPILS)) {
            while (resultSet.next()) {
                list.add(parsePupil(resultSet));
            }
            LOGGER.info("List of pupils complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    private Pupil parsePupil(ResultSet resultSet) {
        Pupil pupil = null;
        try {
            int id = resultSet.getInt("Pupil_ID");
            int classID = resultSet.getInt("class_id");
            String name = resultSet.getString("NAME");
            if (classID == 0) {
                pupil = new Pupil(id, name, null);
            } else {
                PupilClass pupilClass = pupilClassDAO.getPupilClass(classID);
                pupil = new Pupil(id, name, pupilClass);
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
    public Pupil getPupil(int id) {
        LOGGER.info("Reading pupil " + id + " from database.");
        Pupil pupil = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PUPIL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    pupil = parsePupil(resultSet);
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
    public void addPupil(Pupil pupil) {
        LOGGER.info("Inserting pupil " + pupil.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PUPIL)) {
            if (pupil.getPupilClass().getId() != 0) {
                preparedStatement.setInt(1, pupil.getPupilClass().getId());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            preparedStatement.setString(2, pupil.getName());
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
    public void updatePupil(Pupil pupil) {
        LOGGER.info("Updating pupil " + pupil.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
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
    public void deletePupil(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PUPIL)) {
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_MARKS_FOR_DELETING_PUPIL)) {
                LOGGER.info("Deleting marks for pupil " + id + ".");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_ROLES_OF_DELETING_PUPIL)) {
                LOGGER.info("Deleting roles for user " + id + ".");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_USER_FOR_DELETING_PUPIL)) {
                LOGGER.info("Deleting user " + id + ".");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
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
    public List<Pupil> getPupilsByPupilClass(int id) {
        LOGGER.info("Reading pupils for class " + id + ".");
        List<Pupil> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PUPILS_BY_CLASS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupil(resultSet));
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
    public int getCountOfPupils() {
        LOGGER.info("Counting pupils.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
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
    public List<Pupil> getPupilsByPage(int page, int range) {
        LOGGER.info("Reading pupils for page " + page + ".");
        List<Pupil> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PUPILS_BY_PAGE)) {
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupil(resultSet));
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
    public List<Pupil> searchPupils(String val, String param) throws Exception {
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
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching pupils by " + param + ".");
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupil(resultSet));
                }
                LOGGER.info("List of pupils complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

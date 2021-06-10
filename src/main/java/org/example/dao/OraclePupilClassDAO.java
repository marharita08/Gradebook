package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.PupilClass;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OraclePupilClassDAO implements PupilClassDAO {
    private Connection connection;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private static final Logger LOGGER = Logger.getLogger(OraclePupilClassDAO.class.getName());

    /**
     * Read all classes from database and put them into list.
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getAllPupilClasses() {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading all classes from database.");
        List<PupilClass> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_CLASS order by GRADE, NAME");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing classes and put them into list.");
            while (resultSet.next()) {
                list.add(parsePupilClass(resultSet));
            }
            LOGGER.info("List of classes complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    private PupilClass parsePupilClass(ResultSet resultSet) {
        PupilClass pupilClass = null;
        try {
            LOGGER.info("Parsing result set into PupilClass.");
            int id = resultSet.getInt("Class_ID");
            int grade = resultSet.getInt("grade");
            String name = resultSet.getString("NAME");
            pupilClass = new PupilClass(id, grade, name);
            LOGGER.info("Parsing complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return pupilClass;
    }

    /**
     * Read class from database by id.
     * @param id class id
     * @return PupilClass
     */
    @Override
    public PupilClass getPupilClass(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading class " + id + " from database.");
        PupilClass pupilClass = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_CLASS"
                            + " where CLASS_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                pupilClass = parsePupilClass(resultSet);
            }
            LOGGER.info("Reading complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return pupilClass;
    }

    /**
     * Insert new class into database.
     * @param pupilClass adding class
     */
    @Override
    public void addPupilClass(PupilClass pupilClass) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Inserting pupil " + pupilClass.getName() + " into database.");
        String sql = "Insert into LAB3_ROZGHON_CLASS "
                + "values (LAB3_ROZGHON_CLASS_SEQ.nextval, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pupilClass.getGrade());
            preparedStatement.setString(2, pupilClass.getName());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Update class data into database.
     * @param pupilClass editing class
     */
    @Override
    public void updatePupilClass(PupilClass pupilClass) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Updating pupil " + pupilClass.getName() + " into database.");
        String sql = "UPDATE LAB3_ROZGHON_CLASS "
                + "set GRADE = ?, NAME = ? where CLASS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pupilClass.getGrade());
            preparedStatement.setString(2, pupilClass.getName());
            preparedStatement.setInt(3, pupilClass.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Delete class from database.
     * @param id class id
     */
    @Override
    public void deletePupilClass(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Setting class_id=null for pupils from " + id + " class.");
        String sql = "update LAB3_ROZGHON_PUPILS "
                + "set CLASS_ID = null "
                + "where CLASS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting marks for " + id + " class.");
            sql = "Delete from LAB3_ROZGHON_MARK " +
                    "where LESSON_ID in (select LESSON_ID from LAB3_ROZGHON_LESSON " +
                    "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID " +
                    "from LAB3_ROZGHON_SUBJECT_DETAILS where CLASS_ID = ?))";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting lessons for " + id + " class.");
            sql = "Delete from LAB3_ROZGHON_LESSON " +
                "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID " +
                "from LAB3_ROZGHON_SUBJECT_DETAILS where CLASS_ID = ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting subject details for " + id + " class.");
            sql = "delete  from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where CLASS_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting " + id + " class.");
            sql = "Delete from LAB3_ROZGHON_CLASS "
                + "where CLASS_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
           LOGGER.error(throwables.getMessage(), throwables);
        }  finally {
            closeAll();
        }
    }

    /**
     * Read classes from database dy subjects and put them into list.
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getPupilClassesBySubject(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading classes for " + id + " subject.");
        List<PupilClass> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_CLASS " +
                            "join LAB3_ROZGHON_SUBJECT_DETAILS using(CLASS_ID) " +
                            "where SUBJECT_ID = ? " +
                            "order by GRADE, NAME");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing classes and put them into list.");
            while (resultSet.next()) {
                list.add(parsePupilClass(resultSet));
            }
            LOGGER.info("List of classes complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    /**
     * Get total count of classes from database.
     * @return int
     */
    @Override
    public int getCountOfPupilClasses() {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Counting classes.");
        int count = 0;
        String sql = "select count(CLASS_ID) as AMOUNT " +
                "from LAB3_ROZGHON_CLASS ";
        try {
            preparedStatement = connection.prepareStatement(sql);
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
     * Get class list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getPupilClassesByPage(int page, int range) {
        List<PupilClass> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading classes for page " + page + ".");
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_CLASS order by GRADE, NAME) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing classes and put them into list.");
            while (resultSet.next()) {
                list.add(parsePupilClass(resultSet));
            }
            LOGGER.info("List of classes complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    /**
     * Search classes by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<PupilClass>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<PupilClass> searchPupilClasses(String val, String param) throws Exception {
        List<PupilClass> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "id":
                sql = " SELECT * FROM LAB3_ROZGHON_CLASS where CLASS_ID like ? order by GRADE, NAME";
                break;
            case "name":
                sql = "SELECT * FROM LAB3_ROZGHON_CLASS " +
                        "where upper(NAME) like ? order by GRADE, NAME";
                break;
            case "grade":
                sql = "SELECT * FROM LAB3_ROZGHON_CLASS " +
                        "where upper(GRADE) like ? order by GRADE, NAME";
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        connection = ConnectionPool.getInstance().getConnection();
        try {
            LOGGER.info("Searching classes by " + param + ".");
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing classes and put them into list.");
            while (resultSet.next()) {
                list.add(parsePupilClass(resultSet));
            }
            LOGGER.info("List of classes complete.");
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

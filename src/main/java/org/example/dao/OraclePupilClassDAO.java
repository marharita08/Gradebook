package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.PupilClass;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class OraclePupilClassDAO implements PupilClassDAO {
    private static final String GET_ALL_CLASSES = "SELECT * FROM CLASS order by GRADE, NAME";
    private static final String GET_CLASS = "SELECT * FROM CLASS where CLASS_ID=?";
    private static final String INSERT_CLASS = "Insert into CLASS values (CLASS_SEQ.nextval, ?, ?)";
    private static final String UPDATE_CLASS = "UPDATE CLASS set GRADE = ?, NAME = ? where CLASS_ID = ?";
    private static final String UPDATE_PUPILS_OF_DELETING_CLASS = "update PUPIL set CLASS_ID = null where CLASS_ID = ?";
    private static final String DELETE_MARKS_FOR_DELETING_CLASS = "Delete from MARK where LESSON_ID in (select LESSON_ID from LESSON " +
            " where theme_id in (select theme_id from theme " +
            "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID from SUBJECT_DETAILS where CLASS_ID = ?)))";
    private static final String DELETE_LESSONS_FOR_DELETING_CLASS = "Delete from LESSON " +
            " where theme_id in (select theme_id from theme " +
            "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID from SUBJECT_DETAILS where CLASS_ID = ?))";
    private static final String DELETE_THEMES_FOR_DELETING_CLASS = "Delete from theme " +
            "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID from SUBJECT_DETAILS where CLASS_ID = ?)";
    private static final String DELETE_SUBJECT_DETAILS_FOR_DELETING_CLASS = "delete  from SUBJECT_DETAILS where CLASS_ID = ?";
    private static final String DELETE_CLASS = "Delete from CLASS where CLASS_ID = ?";
    private static final String GET_CLASSES_BY_SUBJECT = "SELECT * FROM CLASS " +
            "join SUBJECT_DETAILS using(CLASS_ID) where SUBJECT_ID = ? order by GRADE, NAME";
    private static final String GET_CLASSES_BY_TEACHER = "SELECT distinct CLASS.* FROM CLASS " +
            "join SUBJECT_DETAILS on CLASS.CLASS_ID=SUBJECT_DETAILS.CLASS_ID where TEACHER_ID = ? order by GRADE, NAME";
    private static final String GET_COUNT_OF_CLASSES = "select count(CLASS_ID) as AMOUNT from CLASS ";
    private static final String GET_CLASSES_BY_PAGE = "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
            " (SELECT * FROM CLASS order by GRADE, NAME) p) WHERE rn BETWEEN ? AND ?";
    private static final String SEARCH_CLASSES_BY_ID = "SELECT * FROM CLASS where CLASS_ID like ? order by GRADE, NAME";
    private static final String SEARCH_CLASSES_BY_NAME = "SELECT * FROM CLASS where upper(NAME) like ? order by GRADE, NAME";
    private static final String SEARCH_CLASSES_BY_GRADE = "SELECT * FROM CLASS where upper(GRADE) like ? order by GRADE, NAME";
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(OraclePupilClassDAO.class.getName());

    public OraclePupilClassDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Read all classes from database and put them into list.
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getAllPupilClasses() {
        LOGGER.info("Reading all classes from database.");
        List<PupilClass> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_CLASSES)) {
            while (resultSet.next()) {
                list.add(parsePupilClass(resultSet));
            }
            LOGGER.info("List of classes complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    private PupilClass parsePupilClass(ResultSet resultSet) {
        PupilClass pupilClass = null;
        try {
            int id = resultSet.getInt("Class_ID");
            int grade = resultSet.getInt("grade");
            String name = resultSet.getString("NAME");
            pupilClass = new PupilClass(id, grade, name);
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
        LOGGER.info("Reading class " + id + " from database.");
        PupilClass pupilClass = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLASS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    pupilClass = parsePupilClass(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return pupilClass;
    }

    /**
     * Insert new class into database.
     * @param pupilClass adding class
     */
    @Override
    public void addPupilClass(PupilClass pupilClass) {
        LOGGER.info("Inserting pupil " + pupilClass.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CLASS)) {
            preparedStatement.setInt(1, pupilClass.getGrade());
            preparedStatement.setString(2, pupilClass.getName());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update class data into database.
     * @param pupilClass editing class
     */
    @Override
    public void updatePupilClass(PupilClass pupilClass) {
        LOGGER.info("Updating pupil " + pupilClass.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CLASS)) {
            preparedStatement.setInt(1, pupilClass.getGrade());
            preparedStatement.setString(2, pupilClass.getName());
            preparedStatement.setInt(3, pupilClass.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete class from database.
     * @param id class id
     */
    @Transactional
    @Override
    public void deletePupilClass(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CLASS)) {
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(UPDATE_PUPILS_OF_DELETING_CLASS)) {
                LOGGER.info("Setting class_id=null for pupils from " + id + " class.");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_MARKS_FOR_DELETING_CLASS)) {
                LOGGER.info("Deleting marks for " + id + " class.");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_LESSONS_FOR_DELETING_CLASS)) {
                LOGGER.info("Deleting lessons for " + id + " class.");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_THEMES_FOR_DELETING_CLASS)) {
                LOGGER.info("Deleting themes for " + id + " class.");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(DELETE_SUBJECT_DETAILS_FOR_DELETING_CLASS)) {
                LOGGER.info("Deleting subject details for " + id + " class.");
                preparedStatement1.setInt(1, id);
                preparedStatement1.executeUpdate();
            }
            LOGGER.info("Deleting " + id + " class.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
           LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Read classes from database by subject and put them into list.
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getPupilClassesBySubject(int id) {
        LOGGER.info("Reading classes for " + id + " subject.");
        List<PupilClass> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLASSES_BY_SUBJECT)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupilClass(resultSet));
                }
                LOGGER.info("List of classes complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Read classes from database by teacher and put them into list.
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getPupilClassesByTeacher(int id) {
        LOGGER.info("Reading classes for " + id + " teacher.");
        List<PupilClass> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLASSES_BY_TEACHER)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupilClass(resultSet));
                }
                LOGGER.info("List of classes complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Get total count of classes from database.
     * @return int
     */
    @Override
    public int getCountOfPupilClasses() {
        LOGGER.info("Counting classes.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_CLASSES)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
        LOGGER.info("Reading classes for page " + page + ".");
        List<PupilClass> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLASSES_BY_PAGE)) {
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parsePupilClass(resultSet));
                }
                LOGGER.info("List of classes complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
                sql = SEARCH_CLASSES_BY_ID;
                break;
            case "name":
                sql = SEARCH_CLASSES_BY_NAME;
                break;
            case "grade":
                sql = SEARCH_CLASSES_BY_GRADE;
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching classes by " + param + ".");
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                LOGGER.info("Parsing classes and put them into list.");
                while (resultSet.next()) {
                    list.add(parsePupilClass(resultSet));
                }
                LOGGER.info("List of classes complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

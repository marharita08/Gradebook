package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.SchoolYearDAO;
import org.example.entities.SchoolYear;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostgresSchoolYearDAO implements SchoolYearDAO {
    private static final String GET_ALL_SCHOOL_YEARS = "SELECT * FROM SCHOOL_YEAR order by SCHOOL_YEAR_ID";
    private static final String GET_SCHOOL_YEAR = "SELECT * FROM SCHOOL_YEAR where SCHOOL_YEAR_ID=?";
    private static final String INSERT_SCHOOL_YEAR = "Insert into SCHOOL_YEAR (name, start_date, end_date) values (?, ?, ?)";
    private static final String UPDATE_SCHOOL_YEAR = "UPDATE SCHOOL_YEAR set name = ?, start_date = ?, end_date = ? where SCHOOL_YEAR_id = ?";
    private static final String DELETE_SCHOOL_YEAR = "Delete from SCHOOL_YEAR where SCHOOL_YEAR_id = ?";
    private static final String GET_COUNT_OF_SCHOOL_YEARS = "select count(SCHOOL_YEAR_ID) as AMOUNT from SCHOOL_YEAR ";
    private static final String GET_SCHOOL_YEARS_BY_PAGE = " SELECT * FROM SCHOOL_YEAR ORDER BY SCHOOL_YEAR_ID limit ? offset ?";
    private static final String SEARCH_SCHOOL_YEARS_BY_ID = " SELECT * FROM SCHOOL_YEAR where to_char(SCHOOL_YEAR_id, '9999') like ? order by SCHOOL_YEAR_id";
    private static final String SEARCH_SCHOOL_YEARS_BY_NAME = " SELECT * FROM SCHOOL_YEAR where upper(name) like ? order by SCHOOL_YEAR_id";
    private static final String SEARCH_SCHOOL_YEARS_BY_START_DATE = "SELECT * FROM SCHOOL_YEAR where to_char(START_DATE, 'dd.mm.yyyy') like ? order by START_DATE";
    private static final String SEARCH_SCHOOL_YEARS_BY_END_DATE = "SELECT * FROM SCHOOL_YEAR where to_char(END_DATE, 'dd.mm.yyyy') like ? order by END_DATE";
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(PostgresSchoolYearDAO.class.getName());

    public PostgresSchoolYearDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private SchoolYear parseSchoolYear(ResultSet resultSet) {
        SchoolYear schoolYear = null;
        try {
            int id = resultSet.getInt("SCHOOL_YEAR_ID");
            String name = resultSet.getString("NAME");
            Date startDate = resultSet.getDate("START_DATE");
            Date endDate = resultSet.getDate("END_DATE");
            schoolYear = new SchoolYear (id, name, startDate, endDate);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return schoolYear;
    }

    /**
     * Read all school years from database and put them into list.
     * @return List<SchoolYear>
     */
    @Override
    public List<SchoolYear> getAllSchoolYears() {
        LOGGER.info("Reading all school years from database.");
        List<SchoolYear> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_SCHOOL_YEARS)) {
            while (resultSet.next()) {
                list.add(parseSchoolYear(resultSet));
            }
            LOGGER.info("List of school years complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Read school year from database by id.
     * @param id school year id
     * @return SchoolYear
     */
    @Override
    public SchoolYear getSchoolYear(int id) {
        LOGGER.info("Reading school year " + id + " from database.");
        SchoolYear schoolYear = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SCHOOL_YEAR)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    schoolYear = parseSchoolYear(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return schoolYear;
    }

    /**
     * Insert new school year into database.
     * @param schoolYear adding school year
     */
    @Override
    public void addSchoolYear(SchoolYear schoolYear) {
        LOGGER.info("Inserting school year " + schoolYear.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SCHOOL_YEAR)) {
            preparedStatement.setString(1, schoolYear.getName());
            preparedStatement.setDate(2, schoolYear.getStartDate());
            preparedStatement.setDate(3, schoolYear.getEndDate());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update school year data into database.
     * @param schoolYear editing school year
     */
    @Override
    public void updateSchoolYear(SchoolYear schoolYear) {
        LOGGER.info("Updating school year " + schoolYear.getName() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SCHOOL_YEAR)) {
            preparedStatement.setString(1, schoolYear.getName());
            preparedStatement.setDate(2, schoolYear.getStartDate());
            preparedStatement.setDate(3, schoolYear.getEndDate());
            preparedStatement.setInt(4, schoolYear.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete school year from database.
     * @param id school year id
     */
    @Override
    @Transactional
    public void deleteSchoolYear(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SCHOOL_YEAR)) {
            LOGGER.info("Deleting school year " + id + "from database.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get total count of school years from database.
     * @return int
     */
    @Override
    public int getCountOfSchoolYears() {
        LOGGER.info("Counting school years.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_SCHOOL_YEARS)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return count;
    }

    /**
     * Get school years list for page.
     * @param page number of page
     * @param range amount of school years per page
     * @return List<SchoolYear>
     */
    @Override
    public List<SchoolYear> getSchoolYearsByPage(int page, int range) {
        List<SchoolYear> list = new ArrayList<>();
        LOGGER.info("Reading school years for " + page + " page.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SCHOOL_YEARS_BY_PAGE)) {
            preparedStatement.setInt(1, range);
            preparedStatement.setInt(2, (page - 1) * range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSchoolYear(resultSet));
                }
                LOGGER.info("List of school years complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Search school years by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<SchoolYear>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<SchoolYear> searchSchoolYears(String val, String param) throws Exception {
        List<SchoolYear> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "name":
                sql = SEARCH_SCHOOL_YEARS_BY_NAME;
                break;
            case "id":
                sql = SEARCH_SCHOOL_YEARS_BY_ID;
                break;
            case "startDate":
                sql = SEARCH_SCHOOL_YEARS_BY_START_DATE;
                break;
            case "endDate":
                sql = SEARCH_SCHOOL_YEARS_BY_END_DATE;
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching school years by " + param + ".");
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSchoolYear(resultSet));
                }
                LOGGER.info("List of school years complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

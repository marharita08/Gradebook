package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.SchoolYearDAO;
import org.example.dao.interfaces.SemesterDAO;
import org.example.entities.SchoolYear;
import org.example.entities.Semester;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostgresSemesterDAO implements SemesterDAO {
    private static final String GET_ALL_SEMESTERS = "SELECT * FROM SEMESTER order by SEMESTER_ID";
    private static final String GET_SEMESTER = "SELECT * FROM SEMESTER where SEMESTER_ID=?";
    private static final String INSERT_SEMESTER = "Insert into SEMESTER (SCHOOL_YEAR_id, name, start_date, end_date) values (?, ?, ?, ?)";
    private static final String UPDATE_SEMESTER = "UPDATE SEMESTER set SCHOOL_YEAR_id = ?, name = ?, start_date = ?, end_date = ? where SEMESTER_ID=?";
    private static final String DELETE_SEMESTER = "Delete from SEMESTER where SEMESTER_id = ?";
    private static final String GET_COUNT_OF_SEMESTERS = "select count(SEMESTER_ID) as AMOUNT from SEMESTER ";
    private static final String GET_SEMESTERS_BY_PAGE = "SELECT * FROM SEMESTER ORDER BY SEMESTER_ID limit ? offset ?";
    private static final String SEARCH_SEMESTERS_BY_ID = " SELECT * FROM SEMESTER where SEMESTER_id like ? order by SEMESTER_id";
    private static final String SEARCH_SEMESTERS_BY_SCHOOL_YEAR = " SELECT * FROM SEMESTER " +
            " join SCHOOL_YEAR using(school_year_id) where upper(school_year.name) like ? order by SEMESTER_id";
    private static final String SEARCH_SEMESTERS_BY_NAME = " SELECT * FROM SEMESTER where upper(name) like ? order by SEMESTER_id";
    private static final String SEARCH_SEMESTERS_BY_START_DATE = "SELECT * FROM SEMESTER where START_DATE like ? order by START_DATE";
    private static final String SEARCH_SEMESTERS_BY_END_DATE = "SELECT * FROM SEMESTER where END_DATE like ? order by END_DATE";
    private final ConnectionPool connectionPool;
    private final SchoolYearDAO schoolYearDAO;
    private static final Logger LOGGER = Logger.getLogger(PostgresSchoolYearDAO.class.getName());

    public PostgresSemesterDAO(ConnectionPool connectionPool, SchoolYearDAO schoolYearDAO) {
        this.connectionPool = connectionPool;
        this.schoolYearDAO = schoolYearDAO;
    }

    private Semester parseSemester(ResultSet resultSet) {
        Semester semester = null;
        try {
            int id = resultSet.getInt("SEMESTER_ID");
            int schoolYearID = resultSet.getInt("SCHOOL_YEAR_ID");
            SchoolYear schoolYear = schoolYearDAO.getSchoolYear(schoolYearID);
            String name = resultSet.getString("NAME");
            Date startDate = resultSet.getDate("START_DATE");
            Date endDate = resultSet.getDate("END_DATE");
            semester = new Semester (id, schoolYear, name, startDate, endDate);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return semester;
    }

    /**
     * Read all semesters from database and put them into list.
     * @return List<Semester>
     */
    @Override
    public List<Semester> getAllSemesters() {
        LOGGER.info("Reading all semesters from database.");
        List<Semester> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_SEMESTERS)) {
            while (resultSet.next()) {
                list.add(parseSemester(resultSet));
            }
            LOGGER.info("List of semesters complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Read semester from database by id.
     * @param id semester id
     * @return Semester
     */
    @Override
    public Semester getSemester(int id) {
        LOGGER.info("Reading semester " + id + " from database.");
        Semester semester = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SEMESTER)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    semester = parseSemester(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return semester;
    }

    /**
     * Insert new semester into database.
     * @param semester adding semester
     */
    @Override
    public void addSemester(Semester semester) {
        LOGGER.info("Inserting semester " + semester.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SEMESTER)) {
            preparedStatement.setInt(1, semester.getSchoolYear().getId());
            preparedStatement.setString(2, semester.getName());
            preparedStatement.setDate(3, semester.getStartDate());
            preparedStatement.setDate(4, semester.getEndDate());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update semester data into database.
     * @param semester editing semester
     */
    @Override
    public void updateSemester(Semester semester) {
        LOGGER.info("Updating school year " + semester.getName() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SEMESTER)) {
            preparedStatement.setInt(1, semester.getSchoolYear().getId());
            preparedStatement.setString(2, semester.getName());
            preparedStatement.setDate(3, semester.getStartDate());
            preparedStatement.setDate(4, semester.getEndDate());
            preparedStatement.setInt(5, semester.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete semester from database.
     * @param id semester id
     */
    @Transactional
    @Override
    public void deleteSemester(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SEMESTER)) {
            LOGGER.info("Deleting semester " + id + "from database.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get total count of semesters from database.
     * @return int
     */
    @Override
    public int getCountOfSemesters() {
        LOGGER.info("Counting semesters.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_SEMESTERS)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return count;
    }

    /**
     * Get semesters list for page.
     * @param page number of page
     * @param range amount of semesters per page
     * @return List<Semester>
     */
    @Override
    public List<Semester> getSemestersByPage(int page, int range) {
        List<Semester> list = new ArrayList<>();
        LOGGER.info("Reading semesters for " + page + " page.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SEMESTERS_BY_PAGE)) {
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseSemester(resultSet));
                }
                LOGGER.info("List of semesters complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Search semesters by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Semester>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<Semester> searchSemesters(String val, String param) throws Exception {
        List<Semester> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "name":
                sql = SEARCH_SEMESTERS_BY_NAME;
                break;
            case "id":
                sql = SEARCH_SEMESTERS_BY_ID;
                break;
            case "schoolYear":
                sql = SEARCH_SEMESTERS_BY_SCHOOL_YEAR;
                break;
            case "startDate":
                sql = SEARCH_SEMESTERS_BY_START_DATE;
                break;
            case "endDate":
                sql = SEARCH_SEMESTERS_BY_END_DATE;
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
                    list.add(parseSemester(resultSet));
                }
                LOGGER.info("List of school years complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

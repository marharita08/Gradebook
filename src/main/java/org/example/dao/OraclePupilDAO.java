package org.example.dao;

import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.example.entities.SubjectDetails;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OraclePupilDAO implements PupilDAO {
    Connection connection;
    ResultSet resultSet;
    PreparedStatement preparedStatement;
    OraclePupilClassDAO pupilClassDAO;

    public OraclePupilDAO(OraclePupilClassDAO pupilClassDAO) {
        this.pupilClassDAO = pupilClassDAO;
    }

    /**
     * Read all pupils from database and put them into list.
     * @return List<Pupil>
     */
    @Override
    public List<Pupil> getAllPupils() {
        connection = ConnectionPool.getInstance().getConnection();
        List<Pupil> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_PUPILS order by PUPIL_ID");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupil(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
            throwables.printStackTrace();
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
        connection = ConnectionPool.getInstance().getConnection();
        Pupil pupil = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_PUPILS"
                            + " where PUPIL_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                pupil = parsePupil(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return pupil;
    }

    /**
     * Insert new pupil into database.
     * @param pupil adding pupil
     */
    @Override
    public void addPupil(Pupil pupil) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Insert into LAB3_ROZGHON_PUPILS "
                + "values (LAB3_ROZGHON_PUPILS_SEQ.nextval, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (pupil.getPupilClass().getId() != 0) {
                preparedStatement.setInt(1, pupil.getPupilClass().getId());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            preparedStatement.setString(2, pupil.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Update class data into database.
     * @param pupil editing pupil
     */
    @Override
    public void updatePupil(Pupil pupil) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "UPDATE LAB3_ROZGHON_PUPILS "
                + "set CLASS_ID = ?, NAME = ? where PUPIL_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (pupil.getPupilClass().getId() != 0) {
                preparedStatement.setInt(1, pupil.getPupilClass().getId());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            preparedStatement.setString(2, pupil.getName());
            preparedStatement.setInt(3, pupil.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete pupil from database.
     * @param id pupil id
     */
    @Override
    public void deletePupil(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Delete from LAB3_ROZGHON_MARK "
                + "where PUPIL_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "Delete from LAB3_ROZGHON_PUPILS "
                    + "where PUPIL_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Get list of pupils who study in set class.
     * @param id class id
     * @return List<Pupil>
     */
    @Override
    public List<Pupil> getPupilsByPupilClass(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Pupil> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_PUPILS " +
                            "where CLASS_ID = ? order by NAME");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupil(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * Get total count of pupils from database.
     * @return int
     */
    @Override
    public int getCountOfPupils() {
        connection = ConnectionPool.getInstance().getConnection();
        int count = 0;
        String sql = "select count(PUPIL_ID) as AMOUNT " +
                "from LAB3_ROZGHON_PUPILS ";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        List<Pupil> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_PUPILS ORDER BY PUPIL_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupil(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        switch (param) {
            case "id":
                sql = " SELECT * FROM LAB3_ROZGHON_PUPILS where PUPIL_ID like ?" +
                        "order by PUPIL_ID";
                break;
            case "name":
                sql = "SELECT * FROM LAB3_ROZGHON_PUPILS " +
                        "where upper(NAME) like ? order by PUPIL_ID";
                break;
            case "class":
                sql = "SELECT * FROM LAB3_ROZGHON_PUPILS " +
                        "join LAB3_ROZGHON_CLASS c using (CLASS_ID) " +
                        "where upper(c.NAME) like ? order by PUPIL_ID";
                break;
            default:
                throw new Exception("Wrong parameter");
        }
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupil(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    private void closeAll(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

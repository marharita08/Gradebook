package org.example.dao;

import org.example.entities.PupilClass;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OraclePupilClassDAO implements PupilClassDAO {
    Connection connection;
    ResultSet resultSet;
    PreparedStatement preparedStatement;

    /**
     * Read all classes from database and put them into list.
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getAllPupilClasses() {
        connection = ConnectionPool.getInstance().getConnection();
        List<PupilClass> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_CLASS order by GRADE, NAME");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupilClass(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
            throwables.printStackTrace();
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
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
        String sql = "Insert into LAB3_ROZGHON_CLASS "
                + "values (LAB3_ROZGHON_CLASS_SEQ.nextval, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pupilClass.getGrade());
            preparedStatement.setString(2, pupilClass.getName());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Update class data into database.
     * @param pupilClass editing class
     */
    @Override
    public void updatePupilClass(PupilClass pupilClass) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "UPDATE LAB3_ROZGHON_CLASS "
                + "set GRADE = ?, NAME = ? where CLASS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pupilClass.getGrade());
            preparedStatement.setString(2, pupilClass.getName());
            preparedStatement.setInt(3, pupilClass.getId());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Delete class from database.
     * @param id class id
     */
    @Override
    public void deletePupilClass(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "update LAB3_ROZGHON_PUPILS "
                + "set CLASS_ID = null "
                + "where CLASS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sql = "Delete from LAB3_ROZGHON_LESSON " +
                "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID " +
                "from LAB3_ROZGHON_SUBJECT_DETAILS where CLASS_ID = ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sql = "delete  from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where CLASS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sql = "Delete from LAB3_ROZGHON_CLASS "
                + "where CLASS_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Read classes from database dy subjects and put them into list.
     * @return List<PupilClass>
     */
    @Override
    public List<PupilClass> getPupilClassesBySubject(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<PupilClass> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_CLASS " +
                            "join LAB3_ROZGHON_SUBJECT_DETAILS using(CLASS_ID) " +
                            "where SUBJECT_ID = ? " +
                            "order by GRADE, NAME");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupilClass(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }
}

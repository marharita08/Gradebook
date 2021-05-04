package org.example.dao;

import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OraclePupilDAO implements PupilDAO {
    Connection connection = ConnectionPool.getInstance().getConnection();
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

        List<Pupil> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_PUPILS");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupil(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
        }
        return pupil;
    }

    /**
     * Insert new pupil into database.
     * @param pupil adding pupil
     */
    @Override
    public void addPupil(Pupil pupil) {
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
        }
    }

    /**
     * Update class data into database.
     * @param pupil editing pupil
     */
    @Override
    public void updatePupil(Pupil pupil) {
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
        }
    }

    /**
     * Delete pupil from database.
     * @param id pupil id
     */
    @Override
    public void deletePupil(int id) {
        String sql = "Delete from LAB3_ROZGHON_PUPILS "
                + "where PUPIL_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<Pupil> getPupilsByPupilClass(int id) {
        List<Pupil> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_PUPILS where CLASS_ID = ? order by NAME");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parsePupil(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }
}

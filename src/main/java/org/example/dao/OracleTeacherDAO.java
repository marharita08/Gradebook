package org.example.dao;

import org.example.entities.Teacher;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OracleTeacherDAO implements TeacherDAO  {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection =
            ConnectionPool.getInstance().getConnection();

    /**
     * Read all teachers from database and put them into list.
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_TEACHER order by teacher_id");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    private Teacher parseTeacher(ResultSet resultSet) {
        Teacher teacher = null;
        try {
            int id = resultSet.getInt("TEACHER_ID");
            String name = resultSet.getString("NAME");
            String position = resultSet.getString("POSITION");
            int chiefID = resultSet.getInt("CHIEF");
            if (chiefID == 0) {
                teacher = new Teacher(id, name, position, null);
            } else {
                teacher = new Teacher(id, name, position, getTeacher(chiefID));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return teacher;
    }

    /**
     * Read teacher from database by id.
     * @param id teacher's id
     * @return Teacher
     */
    @Override
    public Teacher getTeacher(int id) {
        Teacher teacher = null;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_TEACHER"
                      + " where teacher_id=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                teacher = parseTeacher(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return teacher;
    }

    /**
     * Insert new teacher into database.
     * @param teacher adding teacher
     */
    @Override
    public void addTeacher(Teacher teacher) {
        String sql = "Insert into LAB3_ROZGHON_TEACHER "
               + "values (LAB3_ROZGHON_TEACHER_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setString(2, teacher.getPosition());
            if (teacher.getChief().getId() != 0) {
                preparedStatement.setInt(3, teacher.getChief().getId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Update teacher's data into database.
     * @param teacher editing teacher
     */
    @Override
    public void updateTeacher(Teacher teacher) {
        String sql = "UPDATE LAB3_ROZGHON_TEACHER "
               + "set name = ?, position = ?, chief = ? where teacher_id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setString(2, teacher.getPosition());
            if (teacher.getChief().getId() != 0) {
                preparedStatement.setInt(3, teacher.getChief().getId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
            preparedStatement.setInt(4, teacher.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Delete teacher from database.
     * @param id teacher's id
     */
    @Override
    public void deleteTeacher(int id) {
        String sql = "update LAB3_ROZGHON_TEACHER "
                      + "set CHIEF = null "
                    + "where CHIEF = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sql = "Delete from LAB3_ROZGHON_TEACHER "
                  + "where teacher_id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Get list of teachers who enable to be chief to teacher with set id.
     * @param id teacher's id
     * @return List<Teacher>
     */
    @Override
    public List<Teacher> getEnableChiefs(int id) {
        List<Teacher> list = new ArrayList<>();
        String sql = "select * from (select * from LAB3_ROZGHON_TEACHER minus " +
                "select * from LAB3_ROZGHON_TEACHER" +
                " start with TEACHER_ID = ? " +
                "connect by nocycle prior TEACHER_ID=CHIEF) " +
                "order by teacher_id";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseTeacher(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }
}

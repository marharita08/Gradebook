package org.example.dao;

import org.example.entities.Subject;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OracleSubjectDAO implements SubjectDAO {
    Connection connection;
    ResultSet resultSet;
    PreparedStatement preparedStatement;

    /**
     * Read all subjects from database and put them into list.
     * @return List<Subject>
     */
    @Override
    public List<Subject> getAllSubjects() {
        connection = ConnectionPool.getInstance().getConnection();
        List<Subject> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseSubject(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    private Subject parseSubject(ResultSet resultSet) {
        Subject subject = null;
        try {
            int id = resultSet.getInt("subject_ID");
            String name = resultSet.getString("NAME");
            subject = new Subject(id, name);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subject;
    }

    /**
     * Read subject from database by id.
     * @param id subject id
     * @return Subject
     */
    @Override
    public Subject getSubject(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        Subject subject = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_SUBJECT"
                            + " where SUBJECT_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                subject = parseSubject(resultSet);
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subject;
    }

    /**
     * Insert new subject into database.
     * @param subject adding subject
     */
    @Override
    public void addSubject(Subject subject) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Insert into LAB3_ROZGHON_SUBJECT "
                + "values (LAB3_ROZGHON_SUBJECT_SEQ.nextval, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subject.getName());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Update subject data into database.
     * @param subject editing subject
     */
    @Override
    public void updateSubject(Subject subject) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "UPDATE LAB3_ROZGHON_SUBJECT "
                + "set NAME = ? where SUBJECT_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subject.getName());
            preparedStatement.setInt(2, subject.getId());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Delete subject from database.
     * @param id subject id
     */
    @Override
    public void deleteSubject(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Delete from LAB3_ROZGHON_LESSON " +
                "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID " +
                "from LAB3_ROZGHON_SUBJECT_DETAILS where SUBJECT_ID = ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sql = "delete  from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where SUBJECT_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sql = "Delete from LAB3_ROZGHON_SUBJECT "
                + "where SUBJECT_ID = ?";
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
     * Get subjects that are learned by class with set id.
     * @param id class id
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectByPupilClass(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Subject> subjectList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "select * from LAB3_ROZGHON_SUBJECT " +
                    "join LAB3_ROZGHON_SUBJECT_DETAILS using(subject_id)" +
                    "where CLASS_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                subjectList.add(parseSubject(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subjectList;
    }

    /**
     * Get subjects which are teached by teacher with set id
     * @param id teacher id
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectByTeacher(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Subject> subjectList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "select distinct SUBJECT_ID, NAME " +
                    "from LAB3_ROZGHON_SUBJECT " +
                    "join LAB3_ROZGHON_SUBJECT_DETAILS using(subject_id)" +
                    "where TEACHER_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                subjectList.add(parseSubject(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subjectList;
    }
}

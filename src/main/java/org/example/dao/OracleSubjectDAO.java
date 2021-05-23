package org.example.dao;

import org.example.entities.Subject;
import org.example.entities.SubjectDetails;
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete subject from database.
     * @param id subject id
     */
    @Override
    public void deleteSubject(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Delete from LAB3_ROZGHON_MARK " +
                "where LESSON_ID in (select LESSON_ID " +
                "from LAB3_ROZGHON_LESSON where SUBJECT_DETAILS_ID in (" +
                "select SUBJECT_DETAILS_ID from LAB3_ROZGHON_SUBJECT_DETAILS" +
                " where SUBJECT_ID = ?))";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "Delete from LAB3_ROZGHON_LESSON " +
                    "where SUBJECT_DETAILS_ID in (select SUBJECT_DETAILS_ID " +
                    "from LAB3_ROZGHON_SUBJECT_DETAILS where SUBJECT_ID = ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "delete  from LAB3_ROZGHON_SUBJECT_DETAILS "
                + "where SUBJECT_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "Delete from LAB3_ROZGHON_SUBJECT "
                + "where SUBJECT_ID = ?";
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return subjectList;
    }

    /**
     * Get total count of subject from database.
     * @return int
     */
    @Override
    public int getCountOfSubjects() {
        connection = ConnectionPool.getInstance().getConnection();
        int count = 0;
        String sql = "select count(SUBJECT_ID) as AMOUNT " +
                "from LAB3_ROZGHON_SUBJECT ";
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
     * Get subject list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Subject>
     */
    @Override
    public List<Subject> getSubjectsByPage(int page, int range) {
        List<Subject> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_SUBJECT ORDER BY SUBJECT_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseSubject(resultSet));
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

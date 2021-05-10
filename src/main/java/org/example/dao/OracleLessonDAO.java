package org.example.dao;

import org.example.entities.Lesson;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class OracleLessonDAO implements LessonDAO {
    Connection connection;
    ResultSet resultSet;
    PreparedStatement preparedStatement;
    OracleSubjectDetailsDAO subjectDetailsDAO;

    public OracleLessonDAO(OracleSubjectDetailsDAO subjectDetailsDAO) {
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    /**
     * Read all lessons from database and put them into list.
     * @return List<Lesson>
     */
    @Override
    public List<Lesson> getAllLessons() {
        connection = ConnectionPool.getInstance().getConnection();
        List<Lesson> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_LESSON");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    private Lesson parseLesson(ResultSet resultSet) {
        Lesson lesson = null;
        try {
            int id = resultSet.getInt("lesson_ID");
            int subjectDetailsID = resultSet.getInt("subject_details_id");
            Date data = resultSet.getDate("lesson_date");
            String topic = resultSet.getString("topic");
            lesson = new Lesson(id, subjectDetailsDAO.getSubjectDetails(subjectDetailsID), data, topic);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lesson;
    }

    /**
     * Read lesson from database by id.
     * @param id lesson id
     * @return Lesson
     */
    @Override
    public Lesson getLesson(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        Lesson lesson = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_LESSON"
                            + " where LESSON_ID = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lesson = parseLesson(resultSet);
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lesson;
    }

    /**
     * Insert new lesson into database.
     * @param lesson adding lesson
     */
    @Override
    public void addLesson(Lesson lesson) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Insert into LAB3_ROZGHON_LESSON "
                + "values (LAB3_ROZGHON_LESSON_SEQ.nextval, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Update lesson data into database.
     * @param lesson editing lesson
     */
    @Override
    public void updateLesson(Lesson lesson) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "UPDATE LAB3_ROZGHON_LESSON "
                + "set SUBJECT_DETAILS_ID = ?, LESSON_DATE = ?, TOPIC = ? where LESSON_ID = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lesson.getSubjectDetails().getId());
            preparedStatement.setDate(2, lesson.getDate());
            preparedStatement.setString(3, lesson.getTopic());
            preparedStatement.setInt(4, lesson.getId());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Delete lesson from database.
     * @param id lesson id
     */
    @Override
    public void deleteLesson(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Delete from LAB3_ROZGHON_LESSON "
                + "where LESSON_ID = ?";
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
     * Read lesson from database by class and subject.
     * @param classID class id
     * @param subjectID subject id
     * @return List<Lesson>
     */
    @Override
    public List<Lesson> getLessonsByPupilClassAndSubject(int classID, int subjectID) {
        connection = ConnectionPool.getInstance().getConnection();
        List<Lesson> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "select * from LAB3_ROZGHON_LESSON " +
                    "join LAB3_ROZGHON_SUBJECT_DETAILS using(subject_details_id)" +
                    "where SUBJECT_ID = ? and CLASS_ID = ?");
            preparedStatement.setInt(1, subjectID);
            preparedStatement.setInt(2, classID);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseLesson(resultSet));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }
}

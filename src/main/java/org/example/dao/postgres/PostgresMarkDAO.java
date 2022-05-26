package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.LessonDAO;
import org.example.dao.interfaces.MarkDAO;
import org.example.dao.interfaces.PupilDAO;
import org.example.entities.Lesson;
import org.example.entities.Mark;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresMarkDAO implements MarkDAO {
    private static final String GET_MARK = "SELECT * FROM MARK where MARK_ID = ?";
    private static final String UPSERT_MARK = "Insert into MARK (lesson_id, pupil_id, mark) values (?, ?, ?) " +
            "ON CONFLICT ON CONSTRAINT MARK_UNIQUE " +
            "DO UPDATE SET mark = ? ";

    private static final String INSERT_ABSENT = "Insert into ABSENT (lesson_id, pupil_id) values (?, ?) " +
            "ON CONFLICT DO NOTHING";
    private static final String UPDATE_MARK = "UPDATE MARK set MARK = ? where PUPIL_ID = ? and LESSON_ID = ?";
    private static final String DELETE_MARK = "Delete from MARK where PUPIL_ID = ? and LESSON_ID = ?";
    private static final String DELETE_ABSENT = "Delete from ABSENT where PUPIL_ID = ? and LESSON_ID = ?";
    private static final String GET_MARKS_BY_PUPIL = "SELECT MARK.* FROM MARK " +
            "join LESSON L on MARK.LESSON_ID = L.LESSON_ID " +
            "join THEME T on T.THEME_ID = L.THEME_ID " +
            "join SUBJECT_DETAILS SD on T.SUBJECT_DETAILS_ID = SD.SUBJECT_DETAILS_ID " +
            "join SEMESTER S on SD.SEMESTER_ID = S.SEMESTER_ID and CURRENT_DATE between START_DATE and END_DATE " +
            " where PUPIL_ID = ? " +
            "order by lesson_date";
    private static final String GET_MARKS_BY_LESSON = "select p.PUPIL_ID, COALESCE(MARK_ID, ABSENT_ID) as MARK_ID, M.LESSON_ID, " +
            "CASE WHEN absent_id is not null THEN 'н' ELSE to_char(mark, '99') END as mark " +
            "from PUPIL p " +
            "join CLASS c on p.CLASS_ID = c.CLASS_ID " +
            "join SUBJECT_DETAILS SD on c.CLASS_ID = SD.CLASS_ID " +
            "join THEME t on SD.SUBJECT_DETAILS_ID = t.SUBJECT_DETAILS_ID " +
            "join LESSON l on t.THEME_ID = l.THEME_ID and LESSON_ID = ? " +
            "left join MARK M on p.PUPIL_ID = M.PUPIL_ID and l.LESSON_ID = M.LESSON_ID " +
            "left join absent A on p.PUPIL_ID = A.PUPIL_ID and l.LESSON_ID = A.LESSON_ID " +
            "order by p.NAME";
    private static final String GET_MARKS_BY_THEME_AND_PUPIL = "with tab as ( " +
            "select p.*, MARK_ID, L.LESSON_ID, MARK, LESSON_DATE from PUPIL p " +
            "join CLASS c on p.CLASS_ID = c.CLASS_ID " +
            "join SUBJECT_DETAILS SD on c.CLASS_ID = SD.CLASS_ID " +
            "join THEME t on SD.SUBJECT_DETAILS_ID = t.SUBJECT_DETAILS_ID and THEME_ID = ? " +
            "join LESSON L on t.THEME_ID = L.THEME_ID " +
            "left join MARK M on p.PUPIL_ID = M.PUPIL_ID  and M.LESSON_ID = L.LESSON_ID " +
            "where p.PUPIL_ID = ?) " +
            "select tab.PUPIL_ID, tab.CLASS_ID, tab.NAME, tab.LESSON_DATE, COALESCE(MARK_ID, ABSENT_ID) as MARK_ID, tab.LESSON_ID, " +
            "CASE WHEN absent_id is not null THEN 'н' ELSE to_char(mark, '99') END as mark " +
            "from tab " +
            "left join ABSENT a on a.lesson_id=tab.lesson_id and a.pupil_id=tab.pupil_id " +
            "union " +
            "select PUPIL_ID, CLASS_ID, NAME, null, null, null , to_char(round(avg(MARK)), '99') from tab " +
            "group by PUPIL_ID, CLASS_ID, NAME " +
            "order by LESSON_DATE ";
    private static final String GET_SEMESTER_MARKS = "with tab as (" +
            "select p.PUPIL_ID, p.NAME pupil_name, t.THEME_ID, t.NAME, round(avg(MARK)) Thematic from PUPIL p " +
            "join CLASS c on p.CLASS_ID = c.CLASS_ID " +
            "join SUBJECT_DETAILS SD on c.CLASS_ID = SD.CLASS_ID and SUBJECT_DETAILS_ID = ?" +
            "join THEME t on SD.SUBJECT_DETAILS_ID = t.SUBJECT_DETAILS_ID " +
            "join LESSON L on t.THEME_ID = L.THEME_ID " +
            "left join MARK M on p.PUPIL_ID = M.PUPIL_ID  and M.LESSON_ID = L.LESSON_ID " +
            "group by p.PUPIL_ID, p.NAME, t.THEME_ID, t.NAME) " +
            "select PUPIL_ID, pupil_NAME, round(avg(Thematic)) mark, null mark_id, null lesson_id from tab " +
            "group by PUPIL_ID, pupil_NAME " +
            "order by pupil_name";
    private final LessonDAO lessonDAO;
    private final PupilDAO pupilDAO;
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(PostgresMarkDAO.class.getName());

    public PostgresMarkDAO(PostgresLessonDAO lessonDAO,
                           PostgresPupilDAO pupilDAO,
                           ConnectionPool connectionPool) {
        this.lessonDAO = lessonDAO;
        this.pupilDAO = pupilDAO;
        this.connectionPool = connectionPool;
    }

    private Mark parseMark(ResultSet resultSet) {
        Mark mark = null;
        try {
            int id = resultSet.getInt("mark_ID");
            int lessonID = resultSet.getInt("lesson_id");
            int pupilID = resultSet.getInt("pupil_id");
            String markInt = resultSet.getString("mark");
            if (markInt != null) {
                markInt = markInt.replaceAll("\\s+", " ").trim();
            }
            if (lessonID == 0) {
                mark = new Mark(id, pupilDAO.getPupil(pupilID), null, markInt);
            } else {
                mark = new Mark(id, pupilDAO.getPupil(pupilID), lessonDAO.getLesson(lessonID), markInt);
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return mark;
    }

    /**
     * Read mark from database by id.
     * @param id mark id
     * @return Mark
     */
    @Override
    public Mark getMark(int id) {
        LOGGER.info("Reading mark " + id + " from database.");
        Mark mark = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARK)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    mark = parseMark(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return mark;
    }

    /**
     * Insert new mark into database.
     * @param mark adding mark
     */
    @Override
    public void addMark(Mark mark) throws Exception {
        LOGGER.info("Inserting mark into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPSERT_MARK)) {
            preparedStatement.setInt(1, mark.getLesson().getId());
            preparedStatement.setInt(2, mark.getPupil().getId());
            preparedStatement.setInt(3, Integer.parseInt(mark.getMark()));
            preparedStatement.setInt(4, Integer.parseInt(mark.getMark()));
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            throwException(mark);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Insert absent into database.
     * @param mark adding absent
     */
    @Override
    public void addAbsent(Mark mark) throws Exception {
        LOGGER.info("Inserting mark into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ABSENT)) {
            preparedStatement.setInt(1, mark.getLesson().getId());
            preparedStatement.setInt(2, mark.getPupil().getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            throwException(mark);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Update mark data into database.
     * @param mark editing mark
     */
    @Override
    public void updateMark(Mark mark) throws Exception {
        LOGGER.info("Updating mark " + mark.getId() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MARK)) {
            preparedStatement.setString(1, mark.getMark());
            preparedStatement.setInt(2, mark.getPupil().getId());
            preparedStatement.setInt(3, mark.getLesson().getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLIntegrityConstraintViolationException e) {
            throwException(mark);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    private void throwException(Mark mark) throws Exception {
        Lesson lesson = lessonDAO.getLesson(mark.getLesson().getId());
        Exception exception = new Exception("Pupil "
                + pupilDAO.getPupil(mark.getPupil().getId()).getName()
                + " already has mark for lesson "
                + lesson.getTheme().getSubjectDetails().getSubject().getName() + " "
                + lesson.getDate() + " " + lesson.getTopic() + ".");
        LOGGER.error(exception.getMessage(), exception);
        throw exception;
    }

    /**
     * Delete mark from database.
     * @param mark deleted mark
     */
    @Override
    public void deleteMark(Mark mark) {
        LOGGER.info("Deleting " + mark.getId() + " mark.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MARK)) {
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete mark from database.
     * @param mark deleted absent
     */
    @Override
    public void deleteAbsent(Mark mark) {
        LOGGER.info("Deleting " + mark.getId() + " absent.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ABSENT)) {
            preparedStatement.setInt(1, mark.getPupil().getId());
            preparedStatement.setInt(2, mark.getLesson().getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get marks for pupil with set id.
     * @param id pupil id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksByPupil(int id) {
        LOGGER.info("Reading marks for " + id + " pupil.");
        List<Mark> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARKS_BY_PUPIL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    list.add(parseMark(resultSet));
                }
                LOGGER.info("List of marks complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Get marks for lesson with set id.
     * @param id lesson id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksByLesson(int id) {
        LOGGER.info("Reading marks for " + id + " lesson.");
        List<Mark> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARKS_BY_LESSON)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    list.add(parseMark(resultSet));
                }
                LOGGER.info("List of marks complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Get marks for theme and pupil.
     * @param themeID theme id
     * @param pupilID pupil id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getMarksByThemeAndPupil(int themeID, int pupilID) {
        LOGGER.info("Reading marks for " + themeID + " theme and " + pupilID + " pupil.");
        List<Mark> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MARKS_BY_THEME_AND_PUPIL)) {
            preparedStatement.setInt(1, themeID);
            preparedStatement.setInt(2, pupilID);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseMark(resultSet));
                }
                LOGGER.info("List of marks complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    /**
     * Get semester marks for subject details.
     * @param id subject details id
     * @return List<Mark>
     */
    @Override
    public List<Mark> getSemesterMarks(int id) {
        LOGGER.info("Reading semester marks for " + id + " subject details.");
        List<Mark> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SEMESTER_MARKS)) {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseMark(resultSet));
                }
                LOGGER.info("List of marks complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}

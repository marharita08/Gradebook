package org.example.dao.interfaces;

import org.example.entities.Lesson;

import java.util.List;

public interface LessonDAO {

    /**
     * Read lesson from database by id.
     * @param id lesson id
     * @return Lesson
     */
    Lesson getLesson(int id, String dbName);

    /**
     * Insert new lesson into database.
     * @param lesson adding lesson
     */
    void addLesson(Lesson lesson, String dbName);

    /**
     * Update lesson data into database.
     * @param lesson editing lesson
     */
    void updateLesson(Lesson lesson, String dbName);

    /**
     * Delete lesson from database.
     * @param id lesson id
     */
    void deleteLesson(int id, String dbName);

    /**
     * Read lesson from database by theme.
     * @param id theme id
     * @return List<Lesson>
     */
    List<Lesson> getLessonsByTheme(int id, String dbName);

    /**
     * Count lessons from database by subject details.
     * @param id subject details id
     * @return int count
     */
    int getCountOfLessonsBySubjectDetails(int id, String dbName);
}

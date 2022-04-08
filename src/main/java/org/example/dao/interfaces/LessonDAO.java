package org.example.dao.interfaces;

import org.example.entities.Lesson;

import java.util.List;

public interface LessonDAO {

    /**
     * Read lesson from database by id.
     * @param id lesson id
     * @return Pupil
     */
    Lesson getLesson(int id);

    /**
     * Insert new lesson into database.
     * @param lesson adding lesson
     */
    void addLesson(Lesson lesson);

    /**
     * Update lesson data into database.
     * @param lesson editing lesson
     */
    void updateLesson(Lesson lesson);

    /**
     * Delete lesson from database.
     * @param id lesson id
     */
    void deleteLesson(int id);

    /**
     * Read lesson from database by theme.
     * @param id theme id
     * @return List<Lesson>
     */
    List<Lesson> getLessonsByTheme(int id);
}

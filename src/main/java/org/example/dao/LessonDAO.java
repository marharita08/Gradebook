package org.example.dao;

import org.example.entities.Lesson;

import java.util.List;

public interface LessonDAO {
    /**
     * Read all lessons from database and put them into list.
     * @return List<Lesson>
     */
    List<Lesson> getAllLessons();

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
     * Read lesson from database by class and subject.
     * @param id subject details id
     * @return List<Lesson>
     */
    List<Lesson> getLessonsBySubjectDetails(int id);

    /**
     * Get total count of lessons from database.
     * @return int
     */
    int getCountOfLessons();

    /**
     * Get lesson list for page.
     * @param page number of page
     * @param range amount of lessons per page
     * @param id subject details id
     * @return List<Lesson>
     */
    List<Lesson> getLessonsBySubjectDetailsAndPage(int id, int page, int range);
}

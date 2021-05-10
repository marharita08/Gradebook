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
     * @param classID class id
     * @param subjectID subject id
     * @return List<Lesson>
     */
    List<Lesson> getLessonsByPupilClassAndSubject(int classID, int subjectID);
}

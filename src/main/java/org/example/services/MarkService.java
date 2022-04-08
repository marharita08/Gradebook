package org.example.services;

import org.example.dao.interfaces.LessonDAO;
import org.example.dao.interfaces.MarkDAO;
import org.example.dao.interfaces.PupilDAO;
import org.example.dao.interfaces.ThemeDAO;
import org.example.entities.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class MarkService {
    private final MarkDAO dao;
    private final PupilDAO pupilDAO;
    private final ThemeDAO themeDAO;
    private final LessonDAO lessonDAO;

    public MarkService(MarkDAO dao, PupilDAO pupilDAO, ThemeDAO themeDAO, LessonDAO lessonDAO) {
        this.dao = dao;
        this.pupilDAO = pupilDAO;
        this.themeDAO = themeDAO;
        this.lessonDAO = lessonDAO;
    }

    public Map<String, Map<Integer, List<Mark>>> getMarksForSubject(SubjectDetails subjectDetails) {
        List<Pupil> pupilList = pupilDAO.getPupilsByPupilClass(subjectDetails.getPupilClass().getId());
        List<Theme> themeList = themeDAO.getThemesBySubjectDetails(subjectDetails.getId());
        Map<String, Map<Integer, List<Mark>>> pupilMap = new TreeMap<>();
        for (Pupil pupil:pupilList) {
            Map<Integer, List<Mark>> themeMap = new TreeMap<>();;
            for (Theme theme:themeList) {
                List<Mark> markList = dao.getMarksByThemeAndPupil(theme.getId(), pupil.getId());
                themeMap.put(theme.getId(), markList);
            }
            pupilMap.put(pupil.getName(), themeMap);
        }
        return pupilMap;
    }

    public Map<Integer, List<Lesson>> getLessonsForSubject(SubjectDetails subjectDetails) {
        List<Theme> themeList = themeDAO.getThemesBySubjectDetails(subjectDetails.getId());
        Map<Integer, List<Lesson>> themeMap = new TreeMap<>();;
        for (Theme theme:themeList) {
            List<Lesson> lessonList = lessonDAO.getLessonsByTheme(theme.getId());
            themeMap.put(theme.getId(), lessonList);
        }
        return themeMap;
    }

    public Map<String, Mark> getSemesterMarks(SubjectDetails subjectDetails) {
        List<Mark> semesterMarks = dao.getSemesterMarks(subjectDetails.getId());
        Map<String, Mark> markMap = new TreeMap<>();;
        for (Mark mark:semesterMarks) {
            markMap.put(mark.getPupil().getName(), mark);
        }
        return markMap;
    }
}

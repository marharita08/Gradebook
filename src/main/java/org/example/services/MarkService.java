package org.example.services;

import org.example.dao.interfaces.LessonDAO;
import org.example.dao.interfaces.MarkDAO;
import org.example.dao.interfaces.PupilDAO;
import org.example.dao.interfaces.ThemeDAO;
import org.example.entities.*;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Map<String, Map<Integer, List<Mark>>> getMarksForSubject(SubjectDetails subjectDetails, int page, int marksPerPage, String dbName) {
        List<Pupil> pupilList = pupilDAO.getPupilsByPupilClass(subjectDetails.getPupilClass().getId(), dbName);
        List<Theme> themeList = themeDAO.getThemesBySubjectDetails(subjectDetails.getId(), dbName);
        Map<String, Map<Integer, List<Mark>>> pupilMap = new LinkedHashMap<>();
        int size = 0;
        for (Pupil pupil:pupilList) {
            Map<Integer, List<Mark>> themeMap = new LinkedHashMap<>();
            for (Theme theme:themeList) {
                List<Mark> markList = dao.getMarksByThemeAndPupil(theme.getId(), pupil.getId(), dbName);
                size += markList.size();
                if ((page - 1) * marksPerPage < size && size <= page * marksPerPage) {
                    themeMap.put(theme.getId(), markList);
                }
            }
            pupilMap.put(pupil.getName(), themeMap);
            size = 0;
        }
        return pupilMap;
    }

    public Map<Integer, List<Lesson>> getLessonsForSubject(SubjectDetails subjectDetails, int page, int marksPerPage, String dbName) {
        List<Theme> themeList = themeDAO.getThemesBySubjectDetails(subjectDetails.getId(), dbName);
        Map<Integer, List<Lesson>> themeMap = new TreeMap<>();
        int size = 0;
        for (Theme theme:themeList) {
            List<Lesson> lessonList = lessonDAO.getLessonsByTheme(theme.getId(), dbName);
            size += (lessonList.size() + 1);
            if ((page - 1) * marksPerPage < size && size <= page * marksPerPage) {
                themeMap.put(theme.getId(), lessonList);
            }
        }
        return themeMap;
    }

    public Map<String, Mark> getSemesterMarks(SubjectDetails subjectDetails, String dbName) {
        List<Mark> semesterMarks = dao.getSemesterMarks(subjectDetails.getId(), dbName);
        Map<String, Mark> markMap = new TreeMap<>();
        for (Mark mark:semesterMarks) {
            markMap.put(mark.getPupil().getName(), mark);
        }
        return markMap;
    }

    public void saveMarks(MarkList list, String dbName) throws Exception {
        for (Mark mark : list.getList()) {
            if (!mark.getMark().equals("")) {
                if (mark.getMark().equals("н")) {
                    dao.addAbsent(mark, dbName);
                    dao.deleteMark(mark, dbName);
                } else {
                    dao.addMark(mark, dbName);
                    dao.deleteAbsent(mark, dbName);
                }
            } else if (mark.getMark().equals("") && mark.getId() != 0) {
                dao.deleteAbsent(mark, dbName);
                dao.deleteMark(mark, dbName);
            }
        }
    }

    public int getCountOfMarks(int id, String dbName) {
        return themeDAO.getCountOfThemesBySubjectDetails(id, dbName) + lessonDAO.getCountOfLessonsBySubjectDetails(id, dbName);
    }
}

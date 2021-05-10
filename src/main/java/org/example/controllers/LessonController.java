package org.example.controllers;

import org.example.dao.*;
import org.example.entities.Lesson;
import org.example.entities.SubjectDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LessonController {
    private OracleLessonDAO dao;
    private OracleSubjectDetailsDAO subjectDetailsDAO;

    public LessonController(OracleLessonDAO dao, OracleSubjectDetailsDAO subjectDetailsDAO) {
        this.dao = dao;
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    /**
     * Getting page to view all lessons list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllLessons")
    public ModelAndView viewAllPupils() {
        List<Lesson> list = dao.getAllLessons();
        return new ModelAndView("viewLessonList", "list", list);
    }

    /**
     * Getting page for lesson adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addLesson")
    public ModelAndView addLesson() {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Lesson());
        model.put("list", subjectDetailsDAO.getAllSubjectDetails());
        model.put("selectedSubjectDetails", 0);
        model.put("title", "Add lesson");
        model.put("formAction", "saveAddedLesson");
        model.put("param", "unset");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Getting page for lesson adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addLesson/{id}")
    public ModelAndView addLesson(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Lesson());
        model.put("list", subjectDetailsDAO.getAllSubjectDetails());
        model.put("selectedSubjectDetails", id);
        model.put("title", "Add lesson");
        model.put("formAction", "saveAddedLesson");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving added lesson.
     * @param lesson added class
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedLesson", method = RequestMethod.POST)
    public ModelAndView saveAddedLesson(@ModelAttribute Lesson lesson) {
        dao.addLesson(lesson);
        return new ModelAndView("redirect:/viewAllLessons");
    }

    /**
     * Getting page for lesson editing.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editLesson/{id}")
    public ModelAndView editLesson(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        Lesson lesson = dao.getLesson(id);
        model.put("command", lesson);
        model.put("selectedSubjectDetails", lesson.getSubjectDetails().getId());
        model.put("list", subjectDetailsDAO.getAllSubjectDetails());
        model.put("title", "Edit lesson");
        model.put("formAction", "../saveEditedLesson");
        model.put("param", "unset");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving edited lesson.
     * @param lesson edited lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedLesson", method = RequestMethod.POST)
    public ModelAndView saveEditedLesson(@ModelAttribute Lesson lesson) {
        dao.updateLesson(lesson);
        return new ModelAndView("redirect:/viewAllLessons");
    }

    /**
     * Delete lesson by id.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteLesson/{id}")
    public ModelAndView deleteLesson(@PathVariable int id) {
        dao.deleteLesson(id);
        return new ModelAndView("redirect:/viewAllLessons");
    }

    /**
     * View lessons by class and subject
     * @param classID class id
     * @param subjectID subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewLessonsByPupilClassAndSubject/{classID}/{subjectID}")
    public ModelAndView viewLessonsByPupilClassAndSubject(@PathVariable int classID,
                                                          @PathVariable int subjectID) {
        List<Lesson> list = dao.getLessonsByPupilClassAndSubject(classID, subjectID);
        Map<String, Object> model = new HashMap<>();
        model.put("list", list);
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetailsBySubjectAndPupilClass(subjectID, classID);
        model.put("header", "Lessons of "
                    + subjectDetails.getSubject().getName()
                    + " " + subjectDetails.getPupilClass().getName());
        if (subjectDetails.getTeacher() != null) {
            model.put("teacher", "Teacher: " + subjectDetails.getTeacher().getName());
        }
        return new ModelAndView("lessonList", model);
    }
}

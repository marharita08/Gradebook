package org.example.controllers;

import org.example.dao.*;
import org.example.entities.Lesson;
import org.example.entities.SubjectDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LessonController {
    private OracleLessonDAO dao;
    private OracleSubjectDetailsDAO subjectDetailsDAO;
    private int lessonsPerPage = 20;

    public LessonController(OracleLessonDAO dao, OracleSubjectDetailsDAO subjectDetailsDAO) {
        this.dao = dao;
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    /**
     * Getting page for lesson adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addLesson/{id}")
    public ModelAndView addLesson(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        model.put("command", new Lesson(subjectDetails));
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Add lesson");
        model.put("formAction", "/Gradebook/saveAddedLesson");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving added lesson.
     * @param lesson added lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedLesson", method = RequestMethod.POST)
    public ModelAndView saveAddedLesson(@ModelAttribute Lesson lesson) {
        dao.addLesson(lesson);
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + lesson.getSubjectDetails().getId());
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
        SubjectDetails subjectDetails = lesson.getSubjectDetails();
        model.put("command", lesson);
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Edit lesson");
        model.put("formAction", "/Gradebook/saveEditedLesson");
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
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(lesson.getSubjectDetails().getId());
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + lesson.getSubjectDetails().getId());
    }

    /**
     * Delete lesson by id.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteLesson/{id}")
    public ModelAndView deleteLesson(@PathVariable int id) {
        SubjectDetails subjectDetails = dao.getLesson(id).getSubjectDetails();
        dao.deleteLesson(id);
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + subjectDetails.getId());
    }

    /**
     * View lessons by subject details
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewLessonsBySubjectDetails/{id}")
    public ModelAndView viewLessonsBySubjectDetails(@PathVariable int id, @RequestParam("page") int page) {
        List<Lesson> list;
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfLessons();
        if (count <= lessonsPerPage) {
            list = dao.getLessonsBySubjectDetails(id);
        } else {
            list = dao.getLessonsBySubjectDetailsAndPage(id, page, lessonsPerPage);
        }
        model.put("list", list);
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        model.put("header", "Lessons of "
                    + subjectDetails.getSubject().getName()
                    + " " + subjectDetails.getPupilClass().getName());
        if (subjectDetails.getTeacher() != null) {
            model.put("teacher", "Teacher: " + subjectDetails.getTeacher().getName());
        }
        model.put("subjectDetails", subjectDetails.getId());
        PaginationController paginationController = new PaginationController(count, lessonsPerPage, page);
        model.put("pagination", paginationController.makePagingLinks("/Gradebook/viewLessonsBySubjectDetails/" + subjectDetails.getId()));
        return new ModelAndView("lessonList", model);
    }
}

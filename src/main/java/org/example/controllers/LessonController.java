package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.*;
import org.example.entities.Lesson;
import org.example.entities.SubjectDetails;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LessonController {
    private final LessonDAO dao;
    private final SubjectDetailsDAO subjectDetailsDAO;
    private static final int lessonsPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(LessonController.class.getName());

    public LessonController(LessonDAO dao,
                            SubjectDetailsDAO subjectDetailsDAO) {
        this.dao = dao;
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    /**
     * Getting page for lesson adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addLesson/{id}")
    public ModelAndView addLesson(@PathVariable int id) {
        LOGGER.info("Add new lesson for subject details " + id + ".");
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Lesson(subjectDetails));
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Add lesson");
        model.put("formAction", "../saveAddedLesson");
        LOGGER.info("Printing form for input lesson's data.");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving added lesson.
     * @param lesson added lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedLesson", method = RequestMethod.POST)
    public ModelAndView saveAddedLesson(@ModelAttribute Lesson lesson) {
        LOGGER.info("Saving added lesson.");
        dao.addLesson(lesson);
        LOGGER.info("Redirect to list of lessons for " + lesson.getSubjectDetails().getId() + " subject details.");
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + lesson.getSubjectDetails().getId() + "?page=1");
    }

    /**
     * Getting page for lesson editing.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editLesson/{id}")
    public ModelAndView editLesson(@PathVariable int id) {
        LOGGER.info("Edit lesson " + id + ".");
        Lesson lesson = dao.getLesson(id);
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        SubjectDetails subjectDetails = lesson.getSubjectDetails();
        model.put("command", lesson);
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Edit lesson");
        model.put("formAction", "../saveEditedLesson");
        LOGGER.info("Printing form for changing lesson's data.");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving edited lesson.
     * @param lesson edited lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedLesson", method = RequestMethod.POST)
    public ModelAndView saveEditedLesson(@ModelAttribute Lesson lesson) {
        LOGGER.info("Saving edited lesson.");
        dao.updateLesson(lesson);
        LOGGER.info("Redirect to list of lessons for " + lesson.getSubjectDetails().getId() + " subject details.");
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + lesson.getSubjectDetails().getId() + "?page=1");
    }

    /**
     * Delete lesson by id.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteLesson/{id}")
    public ModelAndView deleteLesson(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting lesson " + id);
        Lesson lesson = dao.getLesson(id);
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        int subjectDetails = lesson.getSubjectDetails().getId();
        dao.deleteLesson(id);
        LOGGER.info("Redirect to list of lessons for " + subjectDetails + " subject details on page " + pageNum + ".");
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + subjectDetails + "?page=" + pageNum);
    }

    /**
     * View lessons by subject details
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewLessonsBySubjectDetails/{id}")
    public ModelAndView viewLessonsBySubjectDetails(@PathVariable int id, @RequestParam("page") int page) {
        LOGGER.info("Getting list of lessons for " + id + " subject details and " + page + " page.");
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        List<Lesson> list;
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfLessons(id);
        if (count <= lessonsPerPage) {
            list = dao.getLessonsBySubjectDetails(id);
        } else {
            list = dao.getLessonsBySubjectDetailsAndPage(id, page, lessonsPerPage);
        }
        model.put("list", list);
        model.put("header", "Lessons of "
                    + subjectDetails.getSubject().getName()
                    + " " + subjectDetails.getPupilClass().getName());
        if (subjectDetails.getTeacher() != null) {
            model.put("teacher", "Teacher: " + subjectDetails.getTeacher().getName());
        }
        model.put("subjectDetails", subjectDetails.getId());
        PaginationController paginationController = new PaginationController(count, lessonsPerPage, page);
        model.put("pagination", paginationController.makePagingLinks("../viewLessonsBySubjectDetails/" + subjectDetails.getId()));
        model.put("pageNum", page);
        LOGGER.info("Printing lessons list.");
        return new ModelAndView("lessonList", model);
    }

    @RequestMapping(value = "/searchLessons")
    @ResponseBody
    public List<Lesson> searchLessons(@RequestParam("val") String val,
                                @RequestParam("param") String param,
                                @RequestParam("sd") int sd) throws Exception {
        LOGGER.info("Searching lessons by " + param + " for " + sd + " subject details.");
        List<Lesson> list;
        if(!val.isEmpty()) {
            list = dao.searchLessons(val, param, sd);
        } else {
            list = dao.getLessonsBySubjectDetailsAndPage(sd, 1, lessonsPerPage);
        }
        return list;
    }
}

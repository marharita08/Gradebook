package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.LessonDAO;
import org.example.dao.interfaces.ThemeDAO;
import org.example.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LessonController {
    private final LessonDAO dao;
    private final ThemeDAO themeDAO;
    private static final Logger LOGGER = Logger.getLogger(LessonController.class.getName());

    public LessonController(LessonDAO dao, ThemeDAO themeDAO) {
        this.dao = dao;
        this.themeDAO = themeDAO;
    }

    /**
     * Getting page for lesson adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/theme/{id}/lesson")
    @Secured("TEACHER")
    public ModelAndView addLesson(@PathVariable int id) {
        LOGGER.info("Add new lesson for theme " + id + ".");
        Theme theme = themeDAO.getTheme(id);
        if (theme == null) {
            LOGGER.error("Theme " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() != theme.getSubjectDetails().getTeacher().getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Lesson(theme));
        model.put("title", "Add lesson");
        model.put("formAction", "../../lesson");
        LOGGER.info("Printing form for input lesson's data.");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving added lesson.
     * @param lesson added lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/lesson", method = RequestMethod.POST)
    @Secured("TEACHER")
    public ModelAndView saveAddedLesson(@ModelAttribute Lesson lesson) {
        LOGGER.info("Saving added lesson.");
        dao.addLesson(lesson);
        LOGGER.info("Redirect to list of lessons for " + lesson.getTheme().getId() + " theme.");
        return new ModelAndView("redirect:/theme/" + lesson.getTheme().getId() + "/lessons");
    }

    /**
     * Getting page for lesson editing.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/lesson/{id}", method = RequestMethod.GET)
    @Secured("TEACHER")
    public ModelAndView editLesson(@PathVariable int id) {
        LOGGER.info("Edit lesson " + id + ".");
        Lesson lesson = dao.getLesson(id);
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Theme theme = lesson.getTheme();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() != theme.getSubjectDetails().getTeacher().getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Edit lesson");
        model.put("command", lesson);
        model.put("formAction", "../lesson/" + lesson.getId());
        LOGGER.info("Printing form for changing lesson's data.");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving edited lesson.
     * @param lesson edited lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/lesson/{id}", method = RequestMethod.POST)
    @Secured("TEACHER")
    public ModelAndView saveEditedLesson(@ModelAttribute Lesson lesson) {
        LOGGER.info("Saving edited lesson.");
        dao.updateLesson(lesson);
        LOGGER.info("Redirect to list of lessons for " + lesson.getTheme().getId() + " theme.");
        return new ModelAndView("redirect:/theme/" + lesson.getTheme().getId() + "/lessons");
    }

    /**
     * Delete lesson by id.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/lesson/{id}/delete")
    @Secured("TEACHER")
    public ModelAndView deleteLesson(@PathVariable int id) {
        LOGGER.info("Deleting lesson " + id);
        Lesson lesson = dao.getLesson(id);
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Theme theme = lesson.getTheme();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() != theme.getSubjectDetails().getTeacher().getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        dao.deleteLesson(id);
        LOGGER.info("Redirect to list of lessons for " + theme.getId() + " theme.");
        return new ModelAndView("redirect:/viewLessonsByTheme/" + theme.getId());
    }

    /**
     * View lessons by subject details
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/theme/{id}/lessons")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewLessonsByTheme(@PathVariable int id) {
        LOGGER.info("Getting list of lessons for " + id + " theme.");
        Theme theme = themeDAO.getTheme(id);
        if (theme == null) {
            LOGGER.error("Theme " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        List<Lesson> list;
        Map<String, Object> model = new HashMap<>();
        list = dao.getLessonsByTheme(id);
        model.put("list", list);
        model.put("theme", theme);
        LOGGER.info("Printing lessons list.");
        return new ModelAndView("viewLessonList", model);
    }
}

package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.LessonDAO;
import org.example.dao.interfaces.SchoolDAO;
import org.example.dao.interfaces.ThemeDAO;
import org.example.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LessonController {
    private final LessonDAO dao;
    private final ThemeDAO themeDAO;
    private final SchoolDAO schoolDAO;
    private static final Logger LOGGER = Logger.getLogger(LessonController.class.getName());

    public LessonController(LessonDAO dao, ThemeDAO themeDAO, SchoolDAO schoolDAO) {
        this.dao = dao;
        this.themeDAO = themeDAO;
        this.schoolDAO = schoolDAO;
    }

    /**
     * Getting page for lesson adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/theme/{id}/lesson")
    @Secured("TEACHER")
    public ModelAndView addLesson(@PathVariable int id) {
        LOGGER.info("Add new lesson for theme " + id + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Theme theme = themeDAO.getTheme(id, dbName);
        if (theme == null) {
            LOGGER.error("Theme " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        if (user.getId() != theme.getSubjectDetails().getTeacher().getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap(theme);
        crumbsMap.put("Додати урок", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", new Lesson(theme));
        model.put("title", "Додати урок");
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.addLesson(lesson, user.getDbName());
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Lesson lesson = dao.getLesson(id, dbName);
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Theme theme = lesson.getTheme();
        if (user.getId() != theme.getSubjectDetails().getTeacher().getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap(theme);
        crumbsMap.put("Редагувати урок", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("title", "Редагувати урок");
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.updateLesson(lesson, user.getDbName());
        LOGGER.info("Redirect to list of lessons for " + lesson.getTheme().getId() + " theme.");
        return new ModelAndView("redirect:/theme/" + lesson.getTheme().getId() + "/lessons");
    }

    /**
     * Delete lesson by id.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/lesson/{id}/delete", method = RequestMethod.POST)
    @Secured("TEACHER")
    public ModelAndView deleteLesson(@PathVariable int id) {
        LOGGER.info("Deleting lesson " + id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Lesson lesson = dao.getLesson(id, user.getDbName());
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Theme theme = lesson.getTheme();
        if (user.getId() != theme.getSubjectDetails().getTeacher().getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        dao.deleteLesson(id, user.getDbName());
        LOGGER.info("Redirect to list of lessons for " + theme.getId() + " theme.");
        return new ModelAndView("redirect:/theme/" + theme.getId() + "/lessons");
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Theme theme = themeDAO.getTheme(id, user.getDbName());
        if (theme == null) {
            LOGGER.error("Theme " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        List<Lesson> list;
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        list = dao.getLessonsByTheme(id, dbName);
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(getBasicCrumbsMap(theme)));
        model.put("list", list);
        model.put("theme", theme);
        LOGGER.info("Printing lessons list.");
        return new ModelAndView("viewLessonList", model);
    }

    private Map<String, String> getBasicCrumbsMap(Theme theme) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        if (user.hasRole("ADMIN")) {
            crumbsMap.put("Деталі предметів", "/subject-details?page=1");
        } else if (user.hasRole("TEACHER")) {
            crumbsMap.put("Деталі предметів", "/teacher/" + user.getId() + "/subject-details");
        } else if (user.hasRole("PUPIL")) {
            crumbsMap.put("Деталі предметів", "/pupil/" + user.getId() + "/subject-details");
        }
        crumbsMap.put("Теми", "/subject-details/" + theme.getSubjectDetails().getId() + "/themes");
        crumbsMap.put("Уроки", "/theme/" + theme.getId() + "/lessons");
        return crumbsMap;
    }
}

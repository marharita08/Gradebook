package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.SubjectDetailsDAO;
import org.example.dao.ThemeDAO;
import org.example.entities.SubjectDetails;
import org.example.entities.Theme;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ThemeController {
    private final ThemeDAO dao;
    private final SubjectDetailsDAO subjectDetailsDAO;
    private static final Logger LOGGER = Logger.getLogger(ThemeController.class.getName());

    public ThemeController(ThemeDAO dao, SubjectDetailsDAO subjectDetailsDAO) {
        this.dao = dao;
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    /**
     * View themes by subject details
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewThemesBySubjectDetails/{id}")
    public ModelAndView viewLessonsBySubjectDetails(@PathVariable int id) {
        LOGGER.info("Getting list of lessons for " + id + " subject details.");
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        List<Theme> list;
        Map<String, Object> model = new HashMap<>();
        list = dao.getThemesBySubjectDetails(id);
        model.put("list", list);
        model.put("header", "Themes for "
                + subjectDetails.getSubject().getName()
                + " " + subjectDetails.getPupilClass().getName());
        if (subjectDetails.getTeacher() != null) {
            model.put("teacher", "Teacher: " + subjectDetails.getTeacher().getName());
        }
        model.put("subjectDetails", subjectDetails.getId());
        LOGGER.info("Printing lessons list.");
        return new ModelAndView("viewThemeList", model);
    }


    /**
     * Getting page for theme adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addTheme/{id}")
    public ModelAndView addTheme(@PathVariable int id) {
        LOGGER.info("Add new theme for subject details " + id + ".");
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details " + id + " not found.");
            return new ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Theme(subjectDetails));
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Add theme");
        model.put("formAction", "saveAddedTheme");
        model.put("toRoot", "");
        LOGGER.info("Printing form for input themes data.");
        return new ModelAndView("themeForm", model);
    }

    /**
     * Saving added theme.
     * @param theme added theme
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedTheme", method = RequestMethod.POST)
    public ModelAndView saveAddedTheme(@ModelAttribute Theme theme) {
        LOGGER.info("Saving added theme.");
        dao.addTheme(theme);
        LOGGER.info("Redirect to theme list.");
        return new ModelAndView("redirect:/viewThemesBySubjectDetails/" + theme.getSubjectDetails().getId());
    }

    /**
     * Getting page for theme editing.
     * @param id theme id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editTheme/{id}")
    public ModelAndView editTheme(@PathVariable int id) {
        LOGGER.info("Edit theme.");
        Theme theme = dao.getTheme(id);
        if (theme == null) {
            LOGGER.error("Theme " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        SubjectDetails subjectDetails = theme.getSubjectDetails();
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", theme);
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Edit theme");
        model.put("formAction", "../saveEditedTheme");
        LOGGER.info("Printing form for changing theme data.");
        return new ModelAndView("themeForm", model);
    }

    /**
     * Saving edited theme.
     * @param theme edited theme
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedTheme", method = RequestMethod.POST)
    public ModelAndView saveEditedTheme(@ModelAttribute Theme theme) {
        LOGGER.info("Saving edited theme.");
        dao.updateTheme(theme);
        LOGGER.info("Redirect to theme list.");
        return new ModelAndView("redirect:/viewThemesBySubjectDetails/" + theme.getSubjectDetails().getId());
    }

    /**
     * Delete semester by id.
     * @param id semester id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteTheme/{id}")
    public ModelAndView deleteTheme(@PathVariable int id) {
        LOGGER.info("Deleting theme " + id + ".");
        Theme theme = dao.getTheme(id);
        if (theme == null) {
            LOGGER.error("Theme " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteTheme(id);
        LOGGER.info("Redirect to theme list.");
        return new ModelAndView("redirect:/viewThemesBySubjectDetails/" + theme.getSubjectDetails().getId());
    }
}

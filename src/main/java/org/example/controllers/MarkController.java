package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.*;
import org.example.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MarkController {
    private final MarkDAO dao;
    private final LessonDAO lessonDAO;
    private final PupilDAO pupilDAO;
    private final SubjectDAO subjectDAO;
    private final SubjectDetailsDAO subjectDetailsDAO;
    private static final Logger LOGGER = Logger.getLogger(MarkController.class.getName());

    public MarkController(MarkDAO dao,
                          LessonDAO lessonDAO,
                          PupilDAO pupilDAO,
                          SubjectDAO subjectDAO,
                          SubjectDetailsDAO subjectDetailsDAO) {
        this.dao = dao;
        this.lessonDAO = lessonDAO;
        this.pupilDAO = pupilDAO;
        this.subjectDAO = subjectDAO;
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    /**
     * Getting page for mark adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addMark/{id}")
    public ModelAndView addMark(@PathVariable int id) {
        LOGGER.info("Add new mark for lesson " + id + ".");
        Lesson lesson = lessonDAO.getLesson(id);
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Mark(lesson));
        model.put("list", pupilDAO.getPupilsByPupilClass(lesson.getTheme().getSubjectDetails().getPupilClass().getId()));
        model.put("selectedPupil", 0);
        model.put("selectedMark", 0);
        model.put("title", "Add mark");
        model.put("formAction", "../saveAddedMark/");
        LOGGER.info("Printing form for input mark's data.");
        return new ModelAndView("markForm", model);
    }

    /**
     * Saving added mark.
     * @param mark added mark
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedMark", method = RequestMethod.POST)
    public ModelAndView saveAddedMark(@ModelAttribute Mark mark) throws Exception {
        LOGGER.info("Saving added mark.");
        dao.addMark(mark);
        LOGGER.info("Redirect to list of marks for " + mark.getLesson().getId() + " lesson.");
        return new ModelAndView("redirect:/viewMarksByLesson/" + mark.getLesson().getId());
    }

    /**
     * Getting page for mark editing.
     * @param id mark id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editMark/{id}")
    public ModelAndView editMark(@PathVariable int id) {
        LOGGER.info("Edit mark " + id + ".");
        Mark mark = dao.getMark(id);
        if (mark == null) {
            LOGGER.error("Mark " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", mark);
        model.put("selectedPupil", mark.getPupil().getId());
        model.put("selectedMark", mark.getMark());
        model.put("list", pupilDAO.getPupilsByPupilClass(mark.getPupil().getPupilClass().getId()));
        model.put("title", "Edit mark");
        model.put("formAction", "../saveEditedMark/");
        LOGGER.info("Printing form for changing mark's data.");
        return new ModelAndView("markForm", model);
    }

    /**
     * Saving edited mark.
     * @param mark edited mark
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedMark", method = RequestMethod.POST)
    public ModelAndView saveEditedMark(@ModelAttribute Mark mark) throws Exception {
        LOGGER.info("Saving edited mark.");
        dao.updateMark(mark);
        LOGGER.info("Redirect to list of marks for " + mark.getLesson().getId() + " lesson.");
        return new ModelAndView("redirect:/viewMarksByLesson/" + mark.getLesson().getId());
    }

    /**
     * Delete mark by id.
     * @param id mark id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteMark/{id}")
    public ModelAndView deleteMark(@PathVariable int id) {
        LOGGER.info("Deleting mark " + id + ".");
        Mark mark = dao.getMark(id);
        if (mark == null) {
            LOGGER.error("Mark " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        int lessonID = mark.getLesson().getId();
        dao.deleteMark(id);
        LOGGER.info("Redirect to list of marks for " + lessonID + " lesson.");
        return new ModelAndView("redirect:/viewMarksByLesson/" + lessonID);
    }

    /**
     * View marks for pupil.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewMarksByPupil/{id}")
    public ModelAndView viewMarksByPupil(@PathVariable int id) {
        LOGGER.info("Getting list of marks for " + id + " pupil.");
        Pupil pupil = pupilDAO.getPupil(id);
        if (pupil == null) {
            LOGGER.error("Pupil " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getMarksByPupil(id));
        model.put("header", "Marks for " + pupil.getName());
        model.put("subjectList", subjectDAO.getSubjectsByPupilClass(pupilDAO.getPupil(id).getPupilClass().getId()));
        LOGGER.info("Printing marks.");
        return new ModelAndView("markListForPupil", model);
    }

    /**
     * View marks for lesson.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewMarksByLesson/{id}")
    public ModelAndView viewMarksByLesson(@PathVariable int id) {
        LOGGER.info("Getting list of marks for " + id + " lesson.");
        Lesson lesson = lessonDAO.getLesson(id);
        if (lesson == null) {
            LOGGER.error("Lesson " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getMarksByLesson(id));
        model.put("header", "Marks for lesson");
        model.put("pupilList", pupilDAO.getPupilsByPupilClass(lesson.getTheme().getSubjectDetails().getPupilClass().getId()));
        model.put("subject", lesson.getTheme().getSubjectDetails().getSubject().getName());
        model.put("teacher", lesson.getTheme().getSubjectDetails().getTeacher().getName());
        model.put("theme", lesson.getTheme().getName());
        model.put("date", lesson.getDate());
        model.put("topic", lesson.getTopic());
        model.put("lesson", id);
        LOGGER.info("Printing marks.");
        return new ModelAndView("markListForLesson", model);
    }

    /**
     * View marks for subject details.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewMarksBySubjectDetails/{id}")
    public ModelAndView viewMarksBySubjectDetails(@PathVariable int id) {
        LOGGER.info("Getting list of marks for " + id + " subject details.");
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("header", "Mark list");
        model.put("lessonList", lessonDAO.getLessonsBySubjectDetails(subjectDetails.getId()));
        model.put("pupilList", pupilDAO.getPupilsByPupilClass(subjectDetails.getPupilClass().getId()));
        model.put("markList", dao.getMarksBySubjectDetails(id));
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        LOGGER.info("Printing marks.");
        return new ModelAndView("marks", model);
    }
}

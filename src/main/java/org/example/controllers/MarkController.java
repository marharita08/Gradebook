package org.example.controllers;

import org.example.dao.*;
import org.example.entities.Lesson;
import org.example.entities.Mark;
import org.example.entities.SubjectDetails;
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
    OracleMarkDAO dao;
    OracleLessonDAO lessonDAO;
    OraclePupilDAO pupilDAO;
    OracleSubjectDAO subjectDAO;
    OracleSubjectDetailsDAO subjectDetailsDAO;

    public MarkController(OracleMarkDAO dao,
                          OracleLessonDAO lessonDAO,
                          OraclePupilDAO pupilDAO,
                          OracleSubjectDAO subjectDAO,
                          OracleSubjectDetailsDAO subjectDetailsDAO) {
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
        Map<String, Object> model = new HashMap<>();
        Lesson lesson = lessonDAO.getLesson(id);
        model.put("command", new Mark(lesson));
        model.put("list", pupilDAO.getPupilsByPupilClass(lesson.getSubjectDetails().getPupilClass().getId()));
        model.put("selectedPupil", 0);
        model.put("selectedMark", 0);
        model.put("title", "Add mark");
        model.put("formAction", "/Gradebook/saveAddedMark/");
        return new ModelAndView("markForm", model);
    }

    /**
     * Saving added mark.
     * @param mark added mark
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedMark", method = RequestMethod.POST)
    public ModelAndView saveAddedMark(@ModelAttribute Mark mark) {
        dao.addMark(mark);
        return new ModelAndView("redirect:/viewMarksByLesson/" + mark.getLesson().getId());
    }

    /**
     * Getting page for mark editing.
     * @param id mark id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editMark/{id}")
    public ModelAndView editMark(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        Mark mark = dao.getMark(id);
        model.put("command", mark);
        model.put("selectedPupil", mark.getPupil().getId());
        model.put("selectedMark", mark.getMark());
        model.put("list", pupilDAO.getPupilsByPupilClass(mark.getPupil().getPupilClass().getId()));
        model.put("title", "Edit mark");
        model.put("formAction", "/Gradebook/saveEditedMark/");
            return new ModelAndView("markForm", model);
    }

    /**
     * Saving edited mark.
     * @param mark edited mark
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedMark", method = RequestMethod.POST)
    public ModelAndView saveEditedMark(@ModelAttribute Mark mark) {
        dao.updateMark(mark);
        return new ModelAndView("redirect:/viewMarksByLesson/" + mark.getLesson().getId());
    }

    /**
     * Delete mark by id.
     * @param id mark id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteMark/{id}")
    public ModelAndView deleteMark(@PathVariable int id) {
        int lessonID = dao.getMark(id).getLesson().getId();
        dao.deleteMark(id);
        return new ModelAndView("redirect:/viewMarksByLesson/" + lessonID);
    }

    /**
     * View marks for pupil.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewMarksByPupil/{id}")
    public ModelAndView viewMarksByPupil(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getMarksByPupil(id));
        model.put("header", "Marks for " + pupilDAO.getPupil(id).getName());
        model.put("subjectList", subjectDAO.getSubjectByPupilClass(pupilDAO.getPupil(id).getPupilClass().getId()));
        return new ModelAndView("markListForPupil", model);
    }

    /**
     * View marks for lesson.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewMarksByLesson/{id}")
    public ModelAndView viewMarksByLesson(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getMarksByLesson(id));
        model.put("header", "Marks for lesson");
        Lesson lesson = lessonDAO.getLesson(id);
        model.put("pupilList", pupilDAO.getPupilsByPupilClass(lesson.getSubjectDetails().getPupilClass().getId()));
        model.put("subject", lesson.getSubjectDetails().getSubject().getName());
        model.put("teacher", lesson.getSubjectDetails().getTeacher().getName());
        model.put("date", lesson.getDate());
        model.put("topic", lesson.getTopic());
        model.put("lesson", id);
        model.put("subjectList", subjectDAO.getSubjectByPupilClass(pupilDAO.getPupil(id).getPupilClass().getId()));
        return new ModelAndView("markListForLesson", model);
    }

    /**
     * View marks for subject details.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewMarksBySubjectDetails/{id}")
    public ModelAndView viewMarksBySubjectDetails(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        model.put("header", "Mark list");
        model.put("lessonList", lessonDAO.getLessonsBySubjectDetails(subjectDetails.getId()));
        model.put("pupilList", pupilDAO.getPupilsByPupilClass(subjectDetails.getPupilClass().getId()));
        model.put("markList", dao.getMarksBySubjectDetails(id));
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        return new ModelAndView("marks", model);
    }
}

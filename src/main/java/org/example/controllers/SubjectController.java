package org.example.controllers;

import org.example.dao.OracleSubjectDAO;
import org.example.entities.PupilClass;
import org.example.entities.Subject;
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
public class SubjectController {
    private OracleSubjectDAO dao;

    public SubjectController(OracleSubjectDAO dao) {
        this.dao = dao;
    }

    /**
     * Getting page to view all subject list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllSubjects")
    public ModelAndView viewAllSubjects() {
        List<Subject> list = dao.getAllSubjects();
        return new ModelAndView("viewSubjectList", "list", list);
    }

    /**
     * Getting page for subject adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addSubject")
    public ModelAndView addSubject() {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new PupilClass());
        model.put("title", "Add subject");
        model.put("formAction", "saveAddedSubject");
        return new ModelAndView("subjectForm", model);
    }

    /**
     * Saving added subject.
     * @param subject added subject
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedSubject", method = RequestMethod.POST)
    public ModelAndView saveAddedSubject(@ModelAttribute Subject subject) {
        dao.addSubject(subject);
        return new ModelAndView("redirect:/viewAllSubjects");
    }

    /**
     * Getting page for subject editing.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editSubject/{id}")
    public ModelAndView editSubject(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("command", dao.getSubject(id));
        model.put("title", "Edit subject");
        model.put("formAction", "../saveEditedSubject");
        return new ModelAndView("subjectForm", model);
    }

    /**
     * Saving edited subject.
     * @param subject edited subject
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedSubject", method = RequestMethod.POST)
    public ModelAndView saveEditedSubject(@ModelAttribute Subject subject) {
        dao.updateSubject(subject);
        return new ModelAndView("redirect:/viewAllSubjects");
    }

    /**
     * Delete subject by id.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteSubject/{id}")
    public ModelAndView deleteSubject(@PathVariable int id) {
        dao.deleteSubject(id);
        return new ModelAndView("redirect:/viewAllSubjects");
    }
}

package org.example.controllers;

import org.example.dao.OraclePupilClassDAO;
import org.example.dao.OracleSubjectDAO;
import org.example.dao.OracleSubjectDetailsDAO;
import org.example.dao.OracleTeacherDAO;
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
public class SubjectDetailsController {
    OracleSubjectDetailsDAO dao;
    OraclePupilClassDAO classDAO;
    OracleTeacherDAO teacherDAO;
    OracleSubjectDAO subjectDAO;

    public SubjectDetailsController(OracleSubjectDetailsDAO dao,
                                    OraclePupilClassDAO classDAO,
                                    OracleTeacherDAO teacherDAO,
                                    OracleSubjectDAO subjectDAO) {
        this.dao = dao;
        this.classDAO = classDAO;
        this.teacherDAO = teacherDAO;
        this.subjectDAO = subjectDAO;
    }

    /**
     * Getting page to view all subject details list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllSubjectDetails")
    public ModelAndView viewAllSubjectDetails() {
        List<SubjectDetails> list = dao.getAllSubjectDetails();
        return new ModelAndView("viewSubjectDetailsList", "list", list);
    }

    /**
     * Getting page for subject details adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addSubjectDetails")
    public ModelAndView addSubjectDetails() {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new SubjectDetails());
        model.put("selectedClass", 0);
        model.put("selectedTeacher", 0);
        model.put("selectedSubject", 0);
        model.put("classList", classDAO.getAllPupilClasses());
        model.put("teacherList", teacherDAO.getAllTeachers());
        model.put("subjectList", subjectDAO.getAllSubjects());
        model.put("title", "Add Subject Details");
        model.put("formAction", "saveAddedSubjectDetails");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving added subject details.
     * @param subjectDetails added subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedSubjectDetails", method = RequestMethod.POST)
    public ModelAndView saveAddedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) {
        dao.addSubjectDetails(subjectDetails);
        return new ModelAndView("redirect:/viewAllSubjectDetails");
    }

    /**
     * Getting page for subject details editing.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editSubjectDetails/{id}", method = RequestMethod.GET)
    public ModelAndView editSubjectDetails(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        SubjectDetails subjectDetails = dao.getSubjectDetails(id);
        model.put("command", subjectDetails);
        model.put("selectedClass", subjectDetails.getPupilClass().getId());
        model.put("selectedTeacher", subjectDetails.getTeacher().getId());
        model.put("selectedSubject", subjectDetails.getSubject().getId());
        model.put("classList", classDAO.getAllPupilClasses());
        model.put("teacherList", teacherDAO.getAllTeachers());
        model.put("subjectList", subjectDAO.getAllSubjects());
        model.put("title", "Edit subject details");
        model.put("formAction", "../saveEditedSubjectDetails");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving edited subject details.
     * @param subjectDetails edited subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedSubjectDetails", method = RequestMethod.POST)
    public ModelAndView saveEditedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) {
        dao.updateSubjectDetails(subjectDetails);
        return new ModelAndView("redirect:/viewAllSubjectDetails");
    }

    /**
     * Delete subject details by id.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteSubjectDetails/{id}")
    public ModelAndView deletePupil(@PathVariable int id) {
        dao.deleteSubjectDetails(id);
        return new ModelAndView("redirect:/viewAllSubjectDetails");
    }
}

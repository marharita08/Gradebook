package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.*;
import org.example.entities.PupilClass;
import org.example.entities.Subject;
import org.example.entities.Teacher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectController {
    private final SubjectDAO dao;
    private final PupilClassDAO pupilClassDAO;
    private final TeacherDAO teacherDAO;
    private static final int subjectPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(SubjectController.class.getName());

    public SubjectController(SubjectDAO dao,
                             PupilClassDAO pupilClassDAO,
                             TeacherDAO teacherDAO) {
        this.dao = dao;
        this.pupilClassDAO = pupilClassDAO;
        this.teacherDAO = teacherDAO;
    }

    /**
     * Getting page to view all subject list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllSubjects")
    public ModelAndView viewAllSubjects(@RequestParam("page") int page) {
        LOGGER.info("Getting list of subjects for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfSubjects();
        PaginationController paginationController = new PaginationController(count, subjectPerPage, page);
        List<Subject> list;
        if(count <= subjectPerPage) {
            list = dao.getAllSubjects();
        } else {
            list = dao.getSubjectsByPage(page, subjectPerPage);
        }
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("viewAllSubjects"));
        model.put("header", "Subject list");
        model.put("pageNum", page);
        model.put("toRoot", "");
        LOGGER.info("Printing subject list.");
        return new ModelAndView("viewSubjectList", model);
    }

    /**
     * Getting page for subject adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addSubject")
    public ModelAndView addSubject() {
        LOGGER.info("Add new subject.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new PupilClass());
        model.put("title", "Add subject");
        model.put("formAction", "saveAddedSubject");
        model.put("toRoot", "");
        LOGGER.info("Printing form for input subject data.");
        return new ModelAndView("subjectForm", model);
    }

    /**
     * Saving added subject.
     * @param subject added subject
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedSubject", method = RequestMethod.POST)
    public ModelAndView saveAddedSubject(@ModelAttribute Subject subject) {
        LOGGER.info("Saving added subject.");
        dao.addSubject(subject);
        LOGGER.info("Redirect to subject list.");
        return new ModelAndView("redirect:/viewAllSubjects?page=1");
    }

    /**
     * Getting page for subject editing.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editSubject/{id}")
    public ModelAndView editSubject(@PathVariable int id) {
        LOGGER.info("Edit subject.");
        Subject subject = dao.getSubject(id);
        if (subject == null) {
            LOGGER.error("Subject " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", dao.getSubject(id));
        model.put("title", "Edit subject");
        model.put("formAction", "../saveEditedSubject");
        model.put("toRoot", "../");
        LOGGER.info("Printing form for changing subject data.");
        return new ModelAndView("subjectForm", model);
    }

    /**
     * Saving edited subject.
     * @param subject edited subject
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedSubject", method = RequestMethod.POST)
    public ModelAndView saveEditedSubject(@ModelAttribute Subject subject) {
        LOGGER.info("Saving edited subject.");
        dao.updateSubject(subject);
        LOGGER.info("Redirect to subject list.");
        return new ModelAndView("redirect:/viewAllSubjects?page=1");
    }

    /**
     * Delete subject by id.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteSubject/{id}")
    public ModelAndView deleteSubject(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting subject " + id + ".");
        Subject subject = dao.getSubject(id);
        if (subject == null) {
            LOGGER.error("Subject " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSubject(id);
        LOGGER.info("Redirect to subject list on page " + pageNum + ".");
        return new ModelAndView("redirect:/viewAllSubjects?page=" + pageNum);
    }

    /**
     * View subject list by class.
     * @param id class id
     * @return ModelAndView
     */
    @RequestMapping(value = "viewSubjectsByPupilClass/{id}")
    public ModelAndView viewSubjectsByPupilClass(@PathVariable int id) {
        LOGGER.info("Getting list of subjects by " + id + " class.");
        PupilClass pupilClass = pupilClassDAO.getPupilClass(id);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("header", "Subjects of " + pupilClass.getName());
        model.put("id", pupilClass.getId());
        model.put("list", dao.getSubjectsByPupilClass(id));
        model.put("param", "class");
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing subject list.");
        return new ModelAndView("viewSubjectList", model);
    }

    /**
     * View subject list by teacher.
     * @param id teacher id
     * @return ModelAndView
     */
    @RequestMapping(value = "viewSubjectsByTeacher/{id}")
    public ModelAndView viewSubjectsByTeacher(@PathVariable int id) {
        LOGGER.info("Getting list of subjects by " + id + " teacher.");
        Teacher teacher = teacherDAO.getTeacher(id);
        if (teacher == null) {
            LOGGER.error("Teacher " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("header", "Subjects of " + teacherDAO.getTeacher(id).getName());
        model.put("list", dao.getSubjectsByTeacher(id));
        model.put("param", "teacher");
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing subject list.");
        return new ModelAndView("viewSubjectList", model);
    }

    @RequestMapping(value = "/searchSubjects")
    @ResponseBody
    public List<Subject> searchSubjects(@RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching subjects by " + param + ".");
        List<Subject> list;
        if(!val.isEmpty()) {
            list = dao.searchSubjects(val, param);
        } else {
            list = dao.getSubjectsByPage(1, subjectPerPage);
        }
        return list;
    }
}

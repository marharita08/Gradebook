package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.PupilClassDAO;
import org.example.dao.interfaces.SchoolDAO;
import org.example.dao.interfaces.SubjectDAO;
import org.example.dao.interfaces.TeacherDAO;
import org.example.entities.PupilClass;
import org.example.entities.School;
import org.example.entities.Subject;
import org.example.entities.User;
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
public class SubjectController {
    private final SubjectDAO dao;
    private final PupilClassDAO pupilClassDAO;
    private final TeacherDAO teacherDAO;
    private final SchoolDAO schoolDAO;
    private static final int subjectPerPage = 25;
    private static final String SUBJECTS_LINK = "/subjects?page=1";
    private static final Logger LOGGER = Logger.getLogger(SubjectController.class.getName());

    public SubjectController(SubjectDAO dao,
                             PupilClassDAO pupilClassDAO,
                             TeacherDAO teacherDAO, SchoolDAO schoolDAO) {
        this.dao = dao;
        this.pupilClassDAO = pupilClassDAO;
        this.teacherDAO = teacherDAO;
        this.schoolDAO = schoolDAO;
    }

    /**
     * Getting page to view all subject list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/subjects")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewAllSubjects(@RequestParam("page") int page) {
        LOGGER.info("Getting list of subjects for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        int count = dao.getCountOfSubjects(dbName);
        PaginationController paginationController = new PaginationController(count, subjectPerPage, page);
        List<Subject> list;
        if (count <= subjectPerPage) {
            list = dao.getAllSubjects(dbName);
        } else {
            list = dao.getSubjectsByPage(page, subjectPerPage, dbName);
        }
        model.put("list", list);
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(getBasicCrumbsMap()));
        model.put("pagination", paginationController.makePagingLinks("viewAllSubjects"));
        model.put("header", "Список предметів");
        model.put("pageNum", page);
        LOGGER.info("Printing subject list.");
        return new ModelAndView("viewSubjectList", model);
    }

    /**
     * Getting page for subject adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject")
    @Secured("ADMIN")
    public ModelAndView addSubject() {
        LOGGER.info("Add new subject.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        School school = schoolDAO.getSchool(Integer.parseInt(user.getDbName()));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Додати предмет", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", new PupilClass());
        model.put("title", "Додати предмет");
        model.put("formAction", "subject");
        LOGGER.info("Printing form for input subject data.");
        return new ModelAndView("subjectForm", model);
    }

    /**
     * Saving added subject.
     * @param subject added subject
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveAddedSubject(@ModelAttribute Subject subject) {
        LOGGER.info("Saving added subject.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.addSubject(subject, user.getDbName());
        LOGGER.info("Redirect to subject list.");
        return new ModelAndView("redirect:" + SUBJECTS_LINK);
    }

    /**
     * Getting page for subject editing.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject/{id}")
    @Secured("ADMIN")
    public ModelAndView editSubject(@PathVariable int id) {
        LOGGER.info("Edit subject.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Subject subject = dao.getSubject(id, dbName);
        if (subject == null) {
            LOGGER.error("Subject " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Редагувати предмет", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", dao.getSubject(id, dbName));
        model.put("title", "Редагувати предмет");
        model.put("formAction", "../subject/" + subject.getId());
        LOGGER.info("Printing form for changing subject data.");
        return new ModelAndView("subjectForm", model);
    }

    /**
     * Saving edited subject.
     * @param subject edited subject
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject/{id}", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveEditedSubject(@ModelAttribute Subject subject) {
        LOGGER.info("Saving edited subject.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.updateSubject(subject, user.getDbName());
        LOGGER.info("Redirect to subject list.");
        return new ModelAndView("redirect:" + SUBJECTS_LINK);
    }

    /**
     * Delete subject by id.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject/{id}/delete", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView deleteSubject(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting subject " + id + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Subject subject = dao.getSubject(id, dbName);
        if (subject == null) {
            LOGGER.error("Subject " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSubject(id, dbName);
        LOGGER.info("Redirect to subject list on page " + pageNum + ".");
        return new ModelAndView("redirect:/subjects?page=" + pageNum);
    }

    @RequestMapping(value = "/subjects/search")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    @ResponseBody
    public List<Subject> searchSubjects(@RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching subjects by " + param + ".");
        List<Subject> list;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        if(!val.isEmpty()) {
            list = dao.searchSubjects(val, param, dbName);
        } else {
            list = dao.getSubjectsByPage(1, subjectPerPage, dbName);
        }
        return list;
    }

    private Map<String, String> getBasicCrumbsMap() {
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Предмети", SUBJECTS_LINK);
        return crumbsMap;
    }
}

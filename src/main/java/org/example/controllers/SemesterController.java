package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.SchoolDAO;
import org.example.dao.interfaces.SchoolYearDAO;
import org.example.dao.interfaces.SemesterDAO;
import org.example.entities.School;
import org.example.entities.SchoolYear;
import org.example.entities.Semester;
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
public class SemesterController {
    private final SemesterDAO dao;
    private final SchoolYearDAO schoolYearDAO;
    private final SchoolDAO schoolDAO;
    private static final int semestersPerPage = 25;
    private static final String SEMESTERS_LINK = "/semesters?page=1";
    private static final Logger LOGGER = Logger.getLogger(SemesterController.class.getName());

    public SemesterController(SemesterDAO dao, SchoolYearDAO schoolYearDAO, SchoolDAO schoolDAO) {
        this.dao = dao;
        this.schoolYearDAO = schoolYearDAO;
        this.schoolDAO = schoolDAO;
    }

    /**
     * Getting page to view all semesters list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/semesters")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewAllSemesters(@RequestParam("page") int page) {
        LOGGER.info("Getting list of semesters for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        int count = dao.getCountOfSemesters(dbName);
        PaginationController paginationController = new PaginationController(count, semestersPerPage, page);
        List<Semester> list;
        if(count <= semestersPerPage) {
            list = dao.getAllSemesters(dbName);
        } else {
            list = dao.getSemestersByPage(page, semestersPerPage, dbName);
        }
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        model.put("list", list);
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(getBasicCrumbsMap()));
        model.put("pagination", paginationController.makePagingLinks("semesters"));
        model.put("header", "Список семестрів");
        model.put("pageNum", page);
        LOGGER.info("Printing semester list.");
        return new ModelAndView("viewSemesterList", model);
    }

    /**
     * Getting page for semester adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/semester")
    @Secured("ADMIN")
    public ModelAndView addSemester() throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        List<SchoolYear> schoolYears = schoolYearDAO.getAllSchoolYears(dbName);
        if (schoolYears == null) {
            throw new Exception("There are no school years. You should add school year firstly.");
        }
        LOGGER.info("Add new semester.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Додати семестр", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", new Semester());
        model.put("list", schoolYears);
        model.put("selectedSchoolYear", 0);
        model.put("title", "Додати семестр");
        model.put("formAction", "semester");
        LOGGER.info("Printing form for input semester data.");
        return new ModelAndView("semesterForm", model);
    }

    /**
     * Saving added semester.
     * @param semester added semester
     * @return ModelAndView
     */
    @RequestMapping(value = "/semester", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveAddedSemester(@ModelAttribute Semester semester) {
        LOGGER.info("Saving added semester.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.addSemester(semester, user.getDbName());
        LOGGER.info("Redirect to semester list.");
        return new ModelAndView("redirect:" + SEMESTERS_LINK);
    }

    /**
     * Getting page for semester editing.
     * @param id semester id
     * @return ModelAndView
     */
    @RequestMapping(value = "/semester/{id}")
    @Secured("ADMIN")
    public ModelAndView editSemester(@PathVariable int id) {
        LOGGER.info("Edit semester.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Semester semester = dao.getSemester(id, dbName);
        if (semester == null) {
            LOGGER.error("Semester " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Редагувати семестр", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", semester);
        model.put("list", schoolYearDAO.getAllSchoolYears(dbName));
        model.put("selectedSchoolYear", semester.getSchoolYear().getId());
        model.put("title", "Редагувати семестр");
        model.put("formAction", "../semester/" + semester.getId());
        LOGGER.info("Printing form for changing semester data.");
        return new ModelAndView("semesterForm", model);
    }

    /**
     * Saving edited semester.
     * @param semester edited semester
     * @return ModelAndView
     */
    @RequestMapping(value = "/semester/{id}", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveEditedSemester(@ModelAttribute Semester semester) {
        LOGGER.info("Saving edited semester.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.updateSemester(semester, user.getDbName());
        LOGGER.info("Redirect to school semester.");
        return new ModelAndView("redirect:" + SEMESTERS_LINK);
    }

    /**
     * Delete semester by id.
     * @param id semester id
     * @return ModelAndView
     */
    @RequestMapping(value = "/semester/{id}/delete", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView deleteSemester(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting semester " + id + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Semester semester = dao.getSemester(id, dbName);
        if (semester == null) {
            LOGGER.error("Semester " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSemester(id, dbName);
        LOGGER.info("Redirect to semester list on page " + pageNum + ".");
        return new ModelAndView("redirect:/semesters?page=" + pageNum);
    }

    @RequestMapping(value = "/semesters/search")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    @ResponseBody
    public List<Semester> searchSemesters(@RequestParam("val") String val,
                                              @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching semesters by " + param + ".");
        List<Semester> list;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        if(!val.isEmpty()) {
            list = dao.searchSemesters(val, param, dbName);
        } else {
            list = dao.getSemestersByPage(1, semestersPerPage, dbName);
        }
        return list;
    }

    private Map<String, String> getBasicCrumbsMap() {
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Семестри", SEMESTERS_LINK);
        return crumbsMap;
    }
}

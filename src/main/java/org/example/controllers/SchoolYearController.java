package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.SchoolDAO;
import org.example.dao.interfaces.SchoolYearDAO;
import org.example.entities.School;
import org.example.entities.SchoolYear;
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
public class SchoolYearController {
    private final SchoolYearDAO dao;
    private final SchoolDAO schoolDAO;
    private static final int schoolYearsPerPage = 25;
    private static final String SCHOOL_YEAR_LINK = "/years?page=1";
    private static final Logger LOGGER = Logger.getLogger(SchoolYearController.class.getName());

    public SchoolYearController(SchoolYearDAO dao, SchoolDAO schoolDAO) {
        this.dao = dao;
        this.schoolDAO = schoolDAO;
    }

    /**
     * Getting page to view all school years list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/years")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewAllSchoolYears(@RequestParam("page") int page) {
        LOGGER.info("Getting list of school years for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        int count = dao.getCountOfSchoolYears(dbName);
        PaginationController paginationController = new PaginationController(count, schoolYearsPerPage, page);
        List<SchoolYear> list;
        if(count <= schoolYearsPerPage) {
            list = dao.getAllSchoolYears(dbName);
        } else {
            list = dao.getSchoolYearsByPage(page, schoolYearsPerPage, dbName);
        }
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        model.put("list", list);
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(getBasicCrumbsMap()));
        model.put("pagination", paginationController.makePagingLinks("years"));
        model.put("header", "Список навчальних років");
        model.put("pageNum", page);
        LOGGER.info("Printing school years list.");
        return new ModelAndView("viewSchoolYearList", model);
    }

    /**
     * Getting page for school year adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/year")
    @Secured("ADMIN")
    public ModelAndView addSchoolYear() {
        LOGGER.info("Add new school year.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        School school = schoolDAO.getSchool(Integer.parseInt(user.getDbName()));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Додати навчальний рік", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", new SchoolYear());
        model.put("title", "Додати навчальний рік");
        model.put("formAction", "year");
        LOGGER.info("Printing form for input school year data.");
        return new ModelAndView("schoolYearForm", model);
    }

    /**
     * Saving added school year.
     * @param schoolYear added school year
     * @return ModelAndView
     */
    @RequestMapping(value = "/year", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveAddedSchoolYear(@ModelAttribute SchoolYear schoolYear) {
        LOGGER.info("Saving added school year.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.addSchoolYear(schoolYear, user.getDbName());
        LOGGER.info("Redirect to school year list.");
        return new ModelAndView("redirect:/years?page=1");
    }

    /**
     * Getting page for school year editing.
     * @param id school year id
     * @return ModelAndView
     */
    @RequestMapping(value = "/year/{id}", method = RequestMethod.GET)
    @Secured("ADMIN")
    public ModelAndView editSchoolYear(@PathVariable int id) {
        LOGGER.info("Edit school year.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        SchoolYear schoolYear = dao.getSchoolYear(id, dbName);
        if (schoolYear == null) {
            LOGGER.error("School year " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Редагувати навчальний рік", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", schoolYear);
        model.put("title", "Редагувати навчальний рік");
        model.put("formAction", "../year/" + id);
        LOGGER.info("Printing form for changing school year data.");
        return new ModelAndView("schoolYearForm", model);
    }

    /**
     * Saving edited school year.
     * @param schoolYear edited school year
     * @return ModelAndView
     */
    @RequestMapping(value = "/year/{id}", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveEditedSchoolYear(@ModelAttribute SchoolYear schoolYear) {
        LOGGER.info("Saving edited school year.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.updateSchoolYear(schoolYear, user.getDbName());
        LOGGER.info("Redirect to school year list.");
        return new ModelAndView("redirect:/years?page=1");
    }

    /**
     * Delete school year by id.
     * @param id school year id
     * @return ModelAndView
     */
    @RequestMapping(value = "/year/{id}/delete", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView deleteSchoolYear(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting school year " + id + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        SchoolYear schoolYear = dao.getSchoolYear(id, dbName);
        if (schoolYear == null) {
            LOGGER.error("School year " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSchoolYear(id, dbName);
        LOGGER.info("Redirect to school year list on page " + pageNum + ".");
        return new ModelAndView("redirect:/years?page=" + pageNum);
    }

    @RequestMapping(value = "/years/search")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    @ResponseBody
    public List<SchoolYear> searchSchoolYears(@RequestParam("val") String val,
                                        @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching school years by " + param + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        List<SchoolYear> list;
        if(!val.isEmpty()) {
            list = dao.searchSchoolYears(val, param, dbName);
        } else {
            list = dao.getSchoolYearsByPage(1, schoolYearsPerPage, dbName);
        }
        return list;
    }

    private Map<String, String> getBasicCrumbsMap() {
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Навчальні роки", SCHOOL_YEAR_LINK);
        return crumbsMap;
    }
}

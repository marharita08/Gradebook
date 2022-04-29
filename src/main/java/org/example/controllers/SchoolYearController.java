package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.SchoolYearDAO;
import org.example.entities.SchoolYear;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SchoolYearController {
    private final SchoolYearDAO dao;
    private static final int schoolYearsPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(SchoolYearController.class.getName());

    public SchoolYearController(SchoolYearDAO dao) {
        this.dao = dao;
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
        int count = dao.getCountOfSchoolYears();
        PaginationController paginationController = new PaginationController(count, schoolYearsPerPage, page);
        List<SchoolYear> list;
        if(count <= schoolYearsPerPage) {
            list = dao.getAllSchoolYears();
        } else {
            list = dao.getSchoolYearsByPage(page, schoolYearsPerPage);
        }
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("years"));
        model.put("header", "School years list");
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
        model.put("command", new SchoolYear());
        model.put("title", "Add school year");
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
        dao.addSchoolYear(schoolYear);
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
        SchoolYear schoolYear = dao.getSchoolYear(id);
        if (schoolYear == null) {
            LOGGER.error("School year " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", schoolYear);
        model.put("title", "Edit school year");
        model.put("formAction", "../saveEditedSchoolYear");
        model.put("toRoot", "../");
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
        dao.updateSchoolYear(schoolYear);
        LOGGER.info("Redirect to school year list.");
        return new ModelAndView("redirect:/years?page=1");
    }

    /**
     * Delete school year by id.
     * @param id school year id
     * @return ModelAndView
     */
    @RequestMapping(value = "/year/{id}/delete")
    @Secured("ADMIN")
    public ModelAndView deleteSchoolYear(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting school year " + id + ".");
        SchoolYear schoolYear = dao.getSchoolYear(id);
        if (schoolYear == null) {
            LOGGER.error("School year " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSchoolYear(id);
        LOGGER.info("Redirect to school year list on page " + pageNum + ".");
        return new ModelAndView("redirect:/years?page=" + pageNum);
    }

    @RequestMapping(value = "/years/search")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    @ResponseBody
    public List<SchoolYear> searchSchoolYears(@RequestParam("val") String val,
                                        @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching school years by " + param + ".");
        List<SchoolYear> list;
        if(!val.isEmpty()) {
            list = dao.searchSchoolYears(val, param);
        } else {
            list = dao.getSchoolYearsByPage(1, schoolYearsPerPage);
        }
        return list;
    }
}

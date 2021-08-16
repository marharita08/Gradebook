package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.SchoolYearDAO;
import org.example.dao.SemesterDAO;
import org.example.entities.Semester;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SemesterController {
    private final SemesterDAO dao;
    private final SchoolYearDAO schoolYearDAO;
    private static final int semestersPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(SemesterController.class.getName());

    public SemesterController(SemesterDAO dao, SchoolYearDAO schoolYearDAO) {
        this.dao = dao;
        this.schoolYearDAO = schoolYearDAO;
    }

    /**
     * Getting page to view all semesters list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllSemesters")
    public ModelAndView viewAllSemesters(@RequestParam("page") int page) {
        LOGGER.info("Getting list of semesters for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfSemesters();
        PaginationController paginationController = new PaginationController(count, semestersPerPage, page);
        List<Semester> list;
        if(count <= semestersPerPage) {
            list = dao.getAllSemesters();
        } else {
            list = dao.getSemestersByPage(page, semestersPerPage);
        }
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("viewAllSemesters"));
        model.put("header", "Semesters list");
        model.put("pageNum", page);
        model.put("toRoot", "");
        LOGGER.info("Printing semester list.");
        return new ModelAndView("viewSemesterList", model);
    }

    /**
     * Getting page for semester adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addSemester")
    public ModelAndView addSemester() {
        LOGGER.info("Add new semester.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Semester());
        model.put("list", schoolYearDAO.getAllSchoolYears());
        model.put("selectedSchoolYear", 0);
        model.put("title", "Add semester");
        model.put("formAction", "saveAddedSemester");
        model.put("toRoot", "");
        LOGGER.info("Printing form for input semester data.");
        return new ModelAndView("semesterForm", model);
    }

    /**
     * Saving added semester.
     * @param semester added semester
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedSemester", method = RequestMethod.POST)
    public ModelAndView saveAddedSemester(@ModelAttribute Semester semester) {
        LOGGER.info("Saving added semester.");
        dao.addSemester(semester);
        LOGGER.info("Redirect to semester list.");
        return new ModelAndView("redirect:/viewAllSemesters?page=1");
    }

    /**
     * Getting page for semester editing.
     * @param id semester id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editSemester/{id}")
    public ModelAndView editSemester(@PathVariable int id) {
        LOGGER.info("Edit semester.");
        Semester semester = dao.getSemester(id);
        if (semester == null) {
            LOGGER.error("Semester " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", semester);
        model.put("list", schoolYearDAO.getAllSchoolYears());
        model.put("selectedSchoolYear", semester.getSchoolYear().getId());
        model.put("title", "Edit semester");
        model.put("formAction", "../saveEditedSemester");
        model.put("toRoot", "../");
        LOGGER.info("Printing form for changing semester data.");
        return new ModelAndView("semesterForm", model);
    }

    /**
     * Saving edited semester.
     * @param semester edited semester
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedSemester", method = RequestMethod.POST)
    public ModelAndView saveEditedSemester(@ModelAttribute Semester semester) {
        LOGGER.info("Saving edited semester.");
        dao.updateSemester(semester);
        LOGGER.info("Redirect to school semester.");
        return new ModelAndView("redirect:/viewAllSemesters?page=1");
    }

    /**
     * Delete semester by id.
     * @param id semester id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteSemester/{id}")
    public ModelAndView deleteSemester(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting semester " + id + ".");
        Semester semester = dao.getSemester(id);
        if (semester == null) {
            LOGGER.error("Semester " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSemester(id);
        LOGGER.info("Redirect to semester list on page " + pageNum + ".");
        return new ModelAndView("redirect:/viewAllSemesters?page=" + pageNum);
    }

    @RequestMapping(value = "/searchSemesters")
    @ResponseBody
    public List<Semester> searchSemesters(@RequestParam("val") String val,
                                              @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching semesters by " + param + ".");
        List<Semester> list;
        if(!val.isEmpty()) {
            list = dao.searchSemesters(val, param);
        } else {
            list = dao.getSemestersByPage(1, semestersPerPage);
        }
        return list;
    }
}
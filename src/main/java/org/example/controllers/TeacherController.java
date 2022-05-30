package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.SchoolDAO;
import org.example.dao.interfaces.TeacherDAO;
import org.example.dao.interfaces.UserDAO;
import org.example.entities.*;
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
public class TeacherController {

    private final TeacherDAO dao;
    private final UserDAO userDAO;
    private final SchoolDAO schoolDAO;
    private static final int teachersPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(TeacherController.class.getName());

    public TeacherController(TeacherDAO dao,
                             UserDAO userDAO, SchoolDAO schoolDAO) {
        this.dao = dao;
        this.userDAO = userDAO;
        this.schoolDAO = schoolDAO;
    }

    /**
     * Getting page to view all teacher list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/teachers")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewAllTeachers(@RequestParam("page") int page) {
        LOGGER.info("Getting list of teachers for " + page + " page.");
        LOGGER.info("Form a model.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        int count = dao.getCountOfTeachers(dbName);
        PaginationController paginationController = new PaginationController(count, teachersPerPage, page);
        List<Teacher> list;
        if(count <= teachersPerPage) {
            list = dao.getAllTeachers(dbName);
        } else {
            list = dao.getTeachersByPage(page, teachersPerPage, dbName);
        }
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Вчителі", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("teachers"));
        model.put("header", "Список вчителів");
        model.put("pageNum", page);
        LOGGER.info("Printing teachers list.");
        return new ModelAndView("viewTeacherList", model);
    }

    /**
     * Delete teacher by id.
     * @param id teacher's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/teacher/{id}/delete")
    @Secured("ADMIN")
    public ModelAndView deleteTeacher(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting teacher " + id + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Teacher teacher = dao.getTeacher(id, dbName);
        if (teacher == null) {
            LOGGER.error("Teacher " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        userDAO.deleteUser(id, dbName);
        LOGGER.info("Redirect to teachers list on page " + pageNum + ".");
        return new ModelAndView("redirect:/teachers?page=" + pageNum);
    }

    @RequestMapping(value = "/teachers/search")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    @ResponseBody
    public List<Teacher> searchTeachers(@RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching teachers by " + param + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        List<Teacher> list;
        if(!val.isEmpty()) {
            list = dao.searchTeachers(val, param, dbName);
        } else {
            list = dao.getTeachersByPage(1, teachersPerPage, dbName);
        }
        return list;
    }
}

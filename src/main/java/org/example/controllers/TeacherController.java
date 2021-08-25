package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.*;
import org.example.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TeacherController {

    private final TeacherDAO dao;
    private final PupilClassDAO pupilClassDAO;
    private final SubjectDAO subjectDAO;
    private static final int teachersPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(TeacherController.class.getName());

    public TeacherController(TeacherDAO dao,
                             PupilClassDAO pupilClassDAO,
                             SubjectDAO subjectDAO) {
        this.dao = dao;
        this.pupilClassDAO = pupilClassDAO;
        this.subjectDAO = subjectDAO;
    }

    /**
     * Getting page to view all teacher list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllTeachers")
    public ModelAndView viewAllTeachers(@RequestParam("page") int page) {
        LOGGER.info("Getting list of teachers for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfTeachers();
        PaginationController paginationController = new PaginationController(count, teachersPerPage, page);
        List<Teacher> list;
        if(count <= teachersPerPage) {
            list = dao.getAllTeachers();
        } else {
            list = dao.getTeachersByPage(page, teachersPerPage);
        }
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("viewAllTeachers"));
        model.put("header", "Teacher list");
        model.put("pageNum", page);
        LOGGER.info("Printing teachers list.");
        return new ModelAndView("viewTeacherList", model);
    }

    /**
     * Getting page for teacher adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addTeacher")
    public ModelAndView addTeacher() {
        LOGGER.info("Add new teacher.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Teacher());
        model.put("title", "Add teacher");
        model.put("formAction", "saveAddedTeacher");
        LOGGER.info("Printing form for input teacher data.");
        return new ModelAndView("teacherForm", model);
    }

    /**
     * Saving added teacher.
     * @param teacher added teacher
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedTeacher", method = RequestMethod.POST)
    public ModelAndView saveAddedTeacher(@ModelAttribute Teacher teacher) {
        LOGGER.info("Saving added teacher.");
        dao.addTeacher(teacher);
        LOGGER.info("Redirect to teacher list.");
        return new ModelAndView("redirect:/viewAllTeachers?page=1");
    }

    /**
     * Getting page for teacher editing.
     * @param id teacher's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editTeacher/{id}")
    public ModelAndView editTeacher(@PathVariable int id) {
        LOGGER.info("Edit teacher.");
        Teacher teacher = dao.getTeacher(id);
        if (teacher == null) {
            LOGGER.error("Teacher " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", teacher);
        model.put("title", "Edit teacher");
        model.put("formAction", "../saveEditedTeacher");
        model.put("toRoot", "../");
        LOGGER.info("Printing form for changing teacher data.");
        return new ModelAndView("teacherForm", model);
    }

    /**
     * Saving edited teacher.
     * @param teacher edited teacher
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedTeacher", method = RequestMethod.POST)
    public ModelAndView saveEditedTeacher(@ModelAttribute Teacher teacher) {
        LOGGER.info("Saving edited teacher.");
        dao.updateTeacher(teacher);
        LOGGER.info("Redirect to teacher list.");
        return new ModelAndView("redirect:/viewAllTeachers?page=1");
    }

    /**
     * Delete teacher by id.
     * @param id teacher's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteTeacher/{id}")
    public ModelAndView deleteTeacher(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting teacher " + id + ".");
        Teacher teacher = dao.getTeacher(id);
        if (teacher == null) {
            LOGGER.error("Teacher " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteTeacher(id);
        LOGGER.info("Redirect to teachers list on page " + pageNum + ".");
        return new ModelAndView("redirect:/viewAllTeachers?page=" + pageNum);
    }

    /**
     * Getting page to view teachers by class.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewTeachersByPupilClass/{id}")
    public ModelAndView viewTeachersByClass(@PathVariable int id) {
        LOGGER.info("Getting list of teachers by " + id + " class.");
        PupilClass pupilClass = pupilClassDAO.getPupilClass(id);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getTeachersByPupilClass(id));
        model.put("header", "Teachers of " + pupilClass.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing teachers list.");
        return new ModelAndView("viewTeacherList", model);
    }

    /**
     * Getting page to view teachers by subject.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewTeachersBySubject/{id}")
    public ModelAndView viewTeachersSubject(@PathVariable int id) {
        LOGGER.info("Getting list of teachers by " + id + " subject.");
        Subject subject = subjectDAO.getSubject(id);
        if (subject == null) {
            LOGGER.error("Subject " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getTeachersBySubject(id));
        model.put("header", "Teachers who teach " + subject.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing teachers list.");
        return new ModelAndView("viewTeacherList", model);
    }

    @RequestMapping(value = "/searchTeachers")
    @ResponseBody
    public List<Teacher> searchTeachers(@RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching teachers by " + param + ".");
        List<Teacher> list;
        if(!val.isEmpty()) {
            list = dao.searchTeachers(val, param);
        } else {
            list = dao.getTeachersByPage(1, teachersPerPage);
        }
        return list;
    }
}

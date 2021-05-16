package org.example.controllers;

import org.example.dao.OraclePupilClassDAO;
import org.example.dao.OracleSubjectDAO;
import org.example.dao.OracleTeacherDAO;
import org.example.entities.Teacher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TeacherController {

    private OracleTeacherDAO dao;
    private OraclePupilClassDAO pupilClassDAO;
    private OracleSubjectDAO subjectDAO;
    private int teachersPerPage = 15;

    public TeacherController(OracleTeacherDAO dao,
                             OraclePupilClassDAO pupilClassDAO,
                             OracleSubjectDAO subjectDAO) {
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
        model.put("pagination", paginationController.makePagingLinks("/Gradebook/viewAllTeachers"));
        return new ModelAndView("viewTeacherList", model);
    }

    /**
     * Getting page to view all teacher list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/showAllTeachers")
    public ModelAndView showAllTeachers(@RequestParam("page") int page) {
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
        model.put("pagination", paginationController.makePagingLinks("/Gradebook/showAllTeachers"));
        model.put("header", "All teachers");
        return new ModelAndView("teacherList", model);
    }

    /**
     * Getting page for teacher adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addTeacher")
    public ModelAndView addTeacher() {
        List<Teacher> list = dao.getAllTeachers();
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Teacher());
        model.put("selectedChief", 0);
        model.put("list", list);
        model.put("title", "Add teacher");
        model.put("formAction", "saveAddedTeacher");
        return new ModelAndView("teacherForm", model);
    }

    /**
     * Saving added teacher.
     * @param teacher added teacher
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedTeacher", method = RequestMethod.POST)
    public ModelAndView saveAddedTeacher(@ModelAttribute Teacher teacher) {
        dao.addTeacher(teacher);
        return new ModelAndView("redirect:/viewAllTeachers");
    }

    /**
     * Getting page for teacher editing.
     * @param id teacher's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editTeacher/{id}")
    public ModelAndView editTeacher(@PathVariable int id) {
        List<Teacher> list = dao.getEnableChiefs(id);
        Map<String, Object> model = new HashMap<>();
        Teacher teacher = dao.getTeacher(id);
        model.put("command", teacher);
        model.put("selectedChief", teacher.getChief().getId());
        model.put("list", list);
        model.put("title", "Edit teacher");
        model.put("formAction", "../saveEditedTeacher");
        return new ModelAndView("teacherForm", model);
    }

    /**
     * Saving edited teacher.
     * @param teacher edited teacher
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedTeacher", method = RequestMethod.POST)
    public ModelAndView saveEditedTeacher(@ModelAttribute Teacher teacher) {
        dao.updateTeacher(teacher);
        return new ModelAndView("redirect:/viewAllTeachers");
    }

    /**
     * Delete teacher by id.
     * @param id teacher's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteTeacher/{id}")
    public ModelAndView deleteTeacher(@PathVariable int id) {
        dao.deleteTeacher(id);
        return new ModelAndView("redirect:/viewAllTeachers");
    }

    /**
     * Getting page to view teachers by class.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewTeachersByPupilClass/{id}")
    public ModelAndView viewTeachersByClass(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getTeachersByPupilClass(id));
        model.put("header", "Teachers of " + pupilClassDAO.getPupilClass(id).getName());
        model.put("pagination", "");
        return new ModelAndView("teacherList", model);
    }

    /**
     * Getting page to view teachers by subject.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewTeachersBySubject/{id}")
    public ModelAndView viewTeachersSubject(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getTeachersBySubject(id));
        model.put("header", "Teachers who teach " + subjectDAO.getSubject(id).getName());
        model.put("pagination", "");
        return new ModelAndView("teacherList", model);
    }
}

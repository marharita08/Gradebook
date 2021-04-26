package org.example.controllers;

import org.example.dao.OracleTeacherDAO;
import org.example.entities.Teacher;
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
public class TeacherController {

    private OracleTeacherDAO dao;

    public TeacherController(OracleTeacherDAO dao) {
        this.dao = dao;
    }

    /**
     * Getting page to view all teacher list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllTeachers")
    public ModelAndView viewAllTeachers() {
        List<Teacher> list = dao.getAllTeachers();
        return new ModelAndView("viewTeacherList", "list", list);
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
        model.put("command", dao.getTeacher(id));
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
}

package org.example.controllers;

import org.example.dao.OraclePupilClassDAO;
import org.example.entities.PupilClass;
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
public class PupilClassController {
    private OraclePupilClassDAO dao;

    public PupilClassController(OraclePupilClassDAO dao) {
        this.dao = dao;
    }

    /**
     * Getting page to view all classes list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllClasses")
    public ModelAndView viewAllClasses() {
        List<PupilClass> list = dao.getAllPupilClasses();
        return new ModelAndView("viewClassList", "list", list);
    }

    /**
     * Getting page for class adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addClass")
    public ModelAndView addClass() {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new PupilClass());
        model.put("title", "Add class");
        model.put("formAction", "saveAddedClass");
        return new ModelAndView("classForm", model);
    }

    /**
     * Saving added class.
     * @param pupilClass added class
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedClass", method = RequestMethod.POST)
    public ModelAndView saveAddedClass(@ModelAttribute PupilClass pupilClass) {
        dao.addPupilClass(pupilClass);
        return new ModelAndView("redirect:/viewAllClasses");
    }

    /**
     * Getting page for class editing.
     * @param id class id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editClass/{id}")
    public ModelAndView editClass(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("command", dao.getPupilClass(id));
        model.put("title", "Edit class");
        model.put("formAction", "../saveEditedClass");
        return new ModelAndView("classForm", model);
    }

    /**
     * Saving edited class.
     * @param pupilClass edited class
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedClass", method = RequestMethod.POST)
    public ModelAndView saveEditedClass(@ModelAttribute PupilClass pupilClass) {
        dao.updatePupilClass(pupilClass);
        return new ModelAndView("redirect:/viewAllClasses");
    }

    /**
     * Delete class by id.
     * @param id class id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteClass/{id}")
    public ModelAndView deleteClass(@PathVariable int id) {
        dao.deletePupilClass(id);
        return new ModelAndView("redirect:/viewAllClasses");
    }
}

package org.example.controllers;

import org.example.dao.OraclePupilClassDAO;
import org.example.dao.OraclePupilDAO;
import org.example.entities.Pupil;
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
public class PupilController {
    OraclePupilDAO dao;
    OraclePupilClassDAO classDAO;

    public PupilController(OraclePupilDAO dao, OraclePupilClassDAO classDAO) {
        this.dao = dao;
        this.classDAO = classDAO;
    }

    /**
     * Getting page to view all pupils list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllPupils")
    public ModelAndView viewAllPupils() {
        List<Pupil> list = dao.getAllPupils();
        return new ModelAndView("viewPupilList", "list", list);
    }

    /**
     * Getting page for pupil adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addPupil")
    public ModelAndView addPupil() {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Pupil());
        model.put("list", classDAO.getAllPupilClasses());
        model.put("selectedClass", 0);
        model.put("title", "Add pupil");
        model.put("formAction", "saveAddedPupil");
        return new ModelAndView("pupilForm", model);
    }

    /**
     * Saving added pupil.
     * @param pupil added class
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedPupil", method = RequestMethod.POST)
    public ModelAndView saveAddedPupil(@ModelAttribute Pupil pupil) {
        dao.addPupil(pupil);
        return new ModelAndView("redirect:/viewAllPupils");
    }

    /**
     * Getting page for pupil editing.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editPupil/{id}")
    public ModelAndView editPupil(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        Pupil pupil = dao.getPupil(id);
        model.put("command", pupil);
        model.put("selectedClass", pupil.getPupilClass().getId());
        model.put("list", classDAO.getAllPupilClasses());
        model.put("title", "Edit pupil");
        model.put("formAction", "../saveEditedPupil");
        return new ModelAndView("pupilForm", model);
    }

    /**
     * Saving edited pupil.
     * @param pupil edited pupil
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedPupil", method = RequestMethod.POST)
    public ModelAndView saveEditedPupil(@ModelAttribute Pupil pupil) {
        dao.updatePupil(pupil);
        return new ModelAndView("redirect:/viewAllPupils");
    }

    /**
     * Delete pupil by id.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deletePupil/{id}")
    public ModelAndView deletePupil(@PathVariable int id) {
        dao.deletePupil(id);
        return new ModelAndView("redirect:/viewAllPupils");
    }

    @RequestMapping(value = "showClassList")
    public ModelAndView showClassList() {
        Map<String, Object> model = new HashMap<>();
        model.put("list", classDAO.getAllPupilClasses());
        model.put("path", "viewPupilsByPupilClass");
        return new ModelAndView("classList", model);
    }

    /**
     * Get list of pupils studying in set class.
     * @param id class id
     * @return List<Pupil>
     */
    @RequestMapping(value = "viewPupilsByPupilClass/{id}")
    public ModelAndView viewPupilsByPupilClass(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("class", classDAO.getPupilClass(id).getName());
        model.put("list", dao.getPupilsByPupilClass(id));
        return new ModelAndView("classPupilList", model);
    }
}

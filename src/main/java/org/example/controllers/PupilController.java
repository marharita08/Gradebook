package org.example.controllers;

import org.example.dao.OraclePupilClassDAO;
import org.example.dao.OraclePupilDAO;
import org.example.dao.OracleUserDAO;
import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.example.entities.Teacher;
import org.example.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PupilController {
    private OraclePupilDAO dao;
    private OraclePupilClassDAO classDAO;
    private OracleUserDAO userDAO;
    private int pupilPerPage = 15;

    public PupilController(OraclePupilDAO dao, OraclePupilClassDAO classDAO, OracleUserDAO userDAO) {
        this.dao = dao;
        this.classDAO = classDAO;
        this.userDAO = userDAO;
    }

    /**
     * Getting page to view all pupils list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllPupils")
    public ModelAndView viewAllPupils(@RequestParam("page") int page) {
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfPupils();
        PaginationController paginationController = new PaginationController(count, pupilPerPage, page);
        List<Pupil> list;
        if(count <= pupilPerPage) {
            list = dao.getAllPupils();
        } else {
            list = dao.getPupilsByPage(page, pupilPerPage);
        }
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("/Gradebook/viewAllPupils"));
        model.put("pageNum", page);
        model.put("header", "Pupil List");
        return new ModelAndView("viewPupilList", model);
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
     * Getting page for pupil adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addPupil/{id}")
    public ModelAndView addPupil(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Pupil());
        model.put("list", classDAO.getAllPupilClasses());
        model.put("selectedClass", id);
        model.put("title", "Add pupil");
        model.put("formAction", "/Gradebook/saveAddedPupil");
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
        return new ModelAndView("redirect:/viewAllPupils?page=1");
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
        if(pupil.getPupilClass() != null) {
            model.put("selectedClass", pupil.getPupilClass().getId());
        } else {
            model.put("selectedClass", 0);
        }
        model.put("list", classDAO.getAllPupilClasses());
        model.put("title", "Edit pupil");
        model.put("formAction", "/Gradebook/saveEditedPupil");
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
        return new ModelAndView("redirect:/viewAllPupils?page=1");
    }

    /**
     * Delete pupil by id.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deletePupil/{id}")
    public ModelAndView deletePupil(@PathVariable int id, @RequestParam("page") int pageNum) {
        dao.deletePupil(id);
        return new ModelAndView("redirect:/viewAllPupils?page=" + pageNum);
    }

    /**
     * Get list of pupils studying in set class.
     * @param id class id
     * @return List<Pupil>
     */
    @RequestMapping(value = "viewPupilsByPupilClass/{id}")
    public ModelAndView viewPupilsByPupilClass(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        PupilClass pupilClass = classDAO.getPupilClass(id);
        model.put("header", "Pupils of " + pupilClass.getName() + " form");
        model.put("list", dao.getPupilsByPupilClass(id));
        model.put("pagination", "");
        model.put("pageNum", 1);
        return new ModelAndView("viewPupilList", model);
    }

    @RequestMapping(value = "/searchPupils")
    @ResponseBody
    public String searchPupils(@RequestParam("page") int pageNum,
                                 @RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Pupil> list;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDAO.getUserByUsername(username);
        if(!val.isEmpty()) {
            list = dao.searchPupils(val, param);
        } else {
            list = dao.getPupilsByPage(pageNum, pupilPerPage);
        }
        for (Pupil pupil:list) {
            int id = pupil.getId();
            sb.append("<tr>");
            if (user.hasRole("ADMIN")) {
                sb.append("<td>").append(id).append("</td>");
                sb.append("<td>");
                if (pupil.getPupilClass() != null) {
                    sb.append(pupil.getPupilClass().getName());
                } else {
                    sb.append("-");
                }
                sb.append("</td>");
            }
            sb.append("<td>").append(pupil.getName()).append("</td>");
            if (user.hasRole("ADMIN")) {
               sb.append("<td>");
                sb.append("<a href=\"editPupil/").append(id).append("\">Edit</a>");
                sb.append("</td>").append("<td>");
                sb.append("<a href=\"deletePupil/").append(id).append("?page=").append(pageNum).append("\">Delete</a></td>");
                sb.append("</td>");
            }
            sb.append("<td>");
            sb.append("<a href=\"/Gradebook/viewMarksByPupil/").append(id).append("\">view marks</a>");
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }
}

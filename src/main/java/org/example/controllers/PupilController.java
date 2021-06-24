package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.*;
import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.example.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PupilController {
    private final PupilDAO dao;
    private final PupilClassDAO classDAO;
    private final UserDAO userDAO;
    private int pupilPerPage = 15;
    private static final Logger LOGGER = Logger.getLogger(PupilController.class.getName());

    public PupilController(PupilDAO dao, PupilClassDAO classDAO, UserDAO userDAO) {
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
        LOGGER.info("Getting list of pupils for " + page + " page.");
        LOGGER.info("Form a model.");
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
        model.put("toRoot", "");
        LOGGER.info("Printing pupil list.");
        return new ModelAndView("viewPupilList", model);
    }

    /**
     * Getting page for pupil adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addPupil")
    public ModelAndView addPupil() {
        LOGGER.info("Add new pupil.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new Pupil());
        model.put("list", classDAO.getAllPupilClasses());
        model.put("selectedClass", 0);
        model.put("title", "Add pupil");
        model.put("formAction", "saveAddedPupil");
        LOGGER.info("Printing form for input pupil data.");
        return new ModelAndView("pupilForm", model);
    }

    /**
     * Saving added pupil.
     * @param pupil added class
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedPupil", method = RequestMethod.POST)
    public ModelAndView saveAddedPupil(@ModelAttribute Pupil pupil) {
        LOGGER.info("Saving added pupil.");
        dao.addPupil(pupil);
        LOGGER.info("Redirect to pupil list.");
        return new ModelAndView("redirect:/viewAllPupils?page=1");
    }

    /**
     * Getting page for pupil editing.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editPupil/{id}")
    public ModelAndView editPupil(@PathVariable int id) {
        LOGGER.info("Edit pupil.");
        Pupil pupil = dao.getPupil(id);
        if (pupil == null) {
            LOGGER.error("Pupil " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", pupil);
        if(pupil.getPupilClass() != null) {
            model.put("selectedClass", pupil.getPupilClass().getId());
        } else {
            model.put("selectedClass", 0);
        }
        model.put("list", classDAO.getAllPupilClasses());
        model.put("title", "Edit pupil");
        model.put("formAction", "../saveEditedPupil");
        LOGGER.info("Printing form for changing pupil data.");
        return new ModelAndView("pupilForm", model);
    }

    /**
     * Saving edited pupil.
     * @param pupil edited pupil
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedPupil", method = RequestMethod.POST)
    public ModelAndView saveEditedPupil(@ModelAttribute Pupil pupil) {
        LOGGER.info("Saving edited pupil.");
        dao.updatePupil(pupil);
        LOGGER.info("Redirect to pupil list.");
        return new ModelAndView("redirect:/viewAllPupils?page=1");
    }

    /**
     * Delete pupil by id.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deletePupil/{id}")
    public ModelAndView deletePupil(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting pupil " + id + ".");
        Pupil pupil = dao.getPupil(id);
        if (pupil == null) {
            LOGGER.error("Pupil " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deletePupil(id);
        LOGGER.info("Redirect to pupil list on page " + pageNum + ".");
        return new ModelAndView("redirect:/viewAllPupils?page=" + pageNum);
    }

    /**
     * Get list of pupils studying in set class.
     * @param id class id
     * @return List<Pupil>
     */
    @RequestMapping(value = "viewPupilsByPupilClass/{id}")
    public ModelAndView viewPupilsByPupilClass(@PathVariable int id) {
        LOGGER.info("Getting list of pupils by " + id + " class.");
        PupilClass pupilClass = classDAO.getPupilClass(id);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("header", "Pupils of " + pupilClass.getName() + " form");
        model.put("list", dao.getPupilsByPupilClass(id));
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing pupil list.");
        return new ModelAndView("viewPupilList", model);
    }

    @RequestMapping(value = "/searchPupils")
    @ResponseBody
    public String searchPupils(@RequestParam("page") int pageNum,
                                 @RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching pupils by " + param + ".");
        StringBuilder sb = new StringBuilder();
        List<Pupil> list;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDAO.getUserByUsername(username);
        if(!val.isEmpty()) {
            list = dao.searchPupils(val, param);
        } else {
            list = dao.getPupilsByPage(pageNum, pupilPerPage);
        }
        LOGGER.info("Forming response.");
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
        LOGGER.info("Printing response.");
        return sb.toString();
    }
}

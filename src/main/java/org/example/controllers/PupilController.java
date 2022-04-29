package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.PupilClassDAO;
import org.example.dao.interfaces.PupilDAO;
import org.example.dao.interfaces.UserDAO;
import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.example.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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
    private static final int pupilPerPage = 25;
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
    @RequestMapping(value = "/pupils")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
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
        model.put("pagination", paginationController.makePagingLinks("pupils"));
        model.put("pageNum", page);
        model.put("header", "Pupil List");
        LOGGER.info("Printing pupil list.");
        return new ModelAndView("viewPupilList", model);
    }

    /**
     * Delete pupil by id.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/pupil/{id}/delete")
    @Secured("ADMIN")
    public ModelAndView deletePupil(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting pupil " + id + ".");
        Pupil pupil = dao.getPupil(id);
        if (pupil == null) {
            LOGGER.error("Pupil " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        userDAO.deleteUser(id);
        LOGGER.info("Redirect to pupil list on page " + pageNum + ".");
        return new ModelAndView("redirect:/viewAllPupils?page=" + pageNum);
    }

    /**
     * Get list of pupils studying in set class.
     * @param id class id
     * @return List<Pupil>
     */
    @RequestMapping(value = "class/{id}/pupils")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewPupilsByPupilClass(@PathVariable int id) {
        LOGGER.info("Getting list of pupils by " + id + " class.");
        PupilClass pupilClass = classDAO.getPupilClass(id);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.hasRole("PUPIL") && !user.hasRole("ADMIN")) {
            Pupil pupil = dao.getPupil(user.getId());
            if (!pupil.getPupilClass().equals(pupilClass)) {
                return new  ModelAndView("errorPage", HttpStatus.FORBIDDEN);
            }
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("header", "Pupils of " + pupilClass.getName() + " form");
        model.put("list", dao.getPupilsByPupilClass(id));
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../../");
        LOGGER.info("Printing pupil list.");
        return new ModelAndView("viewPupilList", model);
    }

    /**
     * Get list of pupils studying in the class with set pupil.
     * @param id pupil id
     * @return List<Pupil>
     */
    @RequestMapping(value = "pupil/{id}/pupils")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewPupilsByPupil(@PathVariable int id) {
        LOGGER.info("Getting list of pupils by " + id + " pupil.");
        Pupil pupil = dao.getPupil(id);
        if (pupil == null) {
            LOGGER.error("Pupil " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        return new ModelAndView("redirect:../../class/" + pupil.getPupilClass().getId() + "/pupils");
    }

    @RequestMapping(value = "pupils/search")
    @ResponseBody
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public List<Pupil> searchPupils(@RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching pupils by " + param + ".");
        List<Pupil> list;
        if(!val.isEmpty()) {
            list = dao.searchPupils(val, param);
        } else {
            list = dao.getPupilsByPage(1, pupilPerPage);
        }
       return list;
    }
}

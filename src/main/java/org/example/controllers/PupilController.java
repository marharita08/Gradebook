package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.PupilClassDAO;
import org.example.dao.interfaces.PupilDAO;
import org.example.dao.interfaces.SchoolDAO;
import org.example.dao.interfaces.UserDAO;
import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.example.entities.School;
import org.example.entities.User;
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
public class PupilController {
    private final PupilDAO dao;
    private final PupilClassDAO classDAO;
    private final UserDAO userDAO;
    private final SchoolDAO schoolDAO;
    private static final int pupilPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(PupilController.class.getName());

    public PupilController(PupilDAO dao, PupilClassDAO classDAO, UserDAO userDAO, SchoolDAO schoolDAO) {
        this.dao = dao;
        this.classDAO = classDAO;
        this.userDAO = userDAO;
        this.schoolDAO = schoolDAO;
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        int count = dao.getCountOfPupils(dbName);
        PaginationController paginationController = new PaginationController(count, pupilPerPage, page);
        List<Pupil> list;
        if(count <= pupilPerPage) {
            list = dao.getAllPupils(dbName);
        } else {
            list = dao.getPupilsByPage(page, pupilPerPage, dbName);
        }
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        model.put("list", list);
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Учні", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("pagination", paginationController.makePagingLinks("pupils"));
        model.put("pageNum", page);
        model.put("header", "Список учнів");
        LOGGER.info("Printing pupil list.");
        return new ModelAndView("viewPupilList", model);
    }

    /**
     * Delete pupil by id.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/pupil/{id}/delete", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView deletePupil(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting pupil " + id + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Pupil pupil = dao.getPupil(id, dbName);
        if (pupil == null) {
            LOGGER.error("Pupil " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        userDAO.deleteUser(id, dbName);
        LOGGER.info("Redirect to pupil list on page " + pageNum + ".");
        return new ModelAndView("redirect:/pupils?page=" + pageNum);
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        PupilClass pupilClass = classDAO.getPupilClass(id, dbName);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        if (user.hasRole("PUPIL") && !user.hasRole("ADMIN")) {
            Pupil pupil = dao.getPupil(user.getId(), dbName);
            if (!pupil.getPupilClass().equals(pupilClass)) {
                return new  ModelAndView("errorPage", HttpStatus.FORBIDDEN);
            }
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Класи", "/classes?page=1");
        crumbsMap.put("Учні", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("header", "Учні " + pupilClass.getName() + " класу");
        model.put("list", dao.getPupilsByPupilClass(id, dbName));
        model.put("pagination", "");
        model.put("pageNum", 1);
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pupil pupil = dao.getPupil(id, user.getDbName());
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        List<Pupil> list;
        if(!val.isEmpty()) {
            list = dao.searchPupils(val, param, dbName);
        } else {
            list = dao.getPupilsByPage(1, pupilPerPage, dbName);
        }
       return list;
    }
}

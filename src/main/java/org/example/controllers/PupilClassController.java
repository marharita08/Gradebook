package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.PupilClassDAO;
import org.example.dao.interfaces.SchoolDAO;
import org.example.dao.interfaces.SubjectDAO;
import org.example.dao.interfaces.TeacherDAO;
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
public class PupilClassController {
    private final PupilClassDAO dao;
    private final SubjectDAO subjectDAO;
    private final TeacherDAO teacherDAO;
    private final SchoolDAO schoolDAO;
    private static final int pupilClassPerPage = 25;
    private static final String CLASSES_LINK = "/classes?page=1";
    private static final Logger LOGGER = Logger.getLogger(PupilClassController.class.getName());

    public PupilClassController(PupilClassDAO dao,
                                SubjectDAO subjectDAO, TeacherDAO teacherDAO, SchoolDAO schoolDAO) {
        this.dao = dao;
        this.subjectDAO = subjectDAO;
        this.teacherDAO = teacherDAO;
        this.schoolDAO = schoolDAO;
    }

    /**
     * Getting page to view all classes list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/classes")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewAllClasses(@RequestParam("page") int page) {
        LOGGER.info("Getting list of classes for " + page + " page.");
        LOGGER.info("Form a model.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfPupilClasses(dbName);
        PaginationController paginationController = new PaginationController(count, pupilClassPerPage, page);
        List<PupilClass> list;
        if(count <= pupilClassPerPage) {
            list = dao.getAllPupilClasses(dbName);
        } else {
            list = dao.getPupilClassesByPage(page, pupilClassPerPage, dbName);
        }
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        model.put("list", list);
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(getBasicCrumbsMap()));
        model.put("pagination", paginationController.makePagingLinks("classes"));
        model.put("header", "Всі класи");
        model.put("pageNum", page);
        LOGGER.info("Printing class list.");
        return new ModelAndView("viewClassList", model);
    }

    /**
     * Get page to view classes by teacher.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/teacher/{id}/classes")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewPupilClassesByTeacher(@PathVariable int id) {
        LOGGER.info("Getting list of classes by " + id + " teacher.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Teacher teacher = teacherDAO.getTeacher(id, dbName);
        if (teacher == null) {
            LOGGER.error("Teacher " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Вчителі", "/teachers?page=1");
        crumbsMap.put("Класи", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("list", dao.getPupilClassesByTeacher(id, dbName));
        model.put("header", "Класи в яких викладає " + teacher.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        LOGGER.info("Printing class list.");
        return new ModelAndView("viewClassList", model);
    }

    /**
     * Getting page for class adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/class")
    @Secured("ADMIN")
    public ModelAndView addClass() {
        LOGGER.info("Add new class.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Додати клас", "");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        School school = schoolDAO.getSchool(Integer.parseInt(user.getDbName()));
        model.put("school", school);
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", new PupilClass());
        model.put("selectedGrade", 1);
        model.put("title", "Додати клас");
        model.put("formAction", "class");
        LOGGER.info("Printing form for input class data.");
        return new ModelAndView("classForm", model);
    }

    /**
     * Saving added class.
     * @param pupilClass added class
     * @return ModelAndView
     */
    @RequestMapping(value = "/class", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveAddedClass(@ModelAttribute PupilClass pupilClass) {
        LOGGER.info("Saving added class.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.addPupilClass(pupilClass, user.getDbName());
        LOGGER.info("Redirect to list of classes.");
        return new ModelAndView("redirect:/classes?page=1");
    }

    /**
     * Getting page for class editing.
     * @param id class id
     * @return ModelAndView
     */
    @RequestMapping(value = "/class/{id}")
    @Secured("ADMIN")
    public ModelAndView editClass(@PathVariable int id) {
        LOGGER.info("Edit class.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        PupilClass pupilClass = dao.getPupilClass(id, dbName);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Редагувати клас", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("command", pupilClass);
        model.put("selectedGrade", pupilClass.getGrade());
        model.put("title", "Редагувати клас");
        model.put("formAction", "../class/" + pupilClass.getId());
        LOGGER.info("Printing form for changing class data.");
        return new ModelAndView("classForm", model);
    }

    /**
     * Saving edited class.
     * @param pupilClass edited class
     * @return ModelAndView
     */
    @RequestMapping(value = "/class/{id}", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveEditedClass(@ModelAttribute PupilClass pupilClass) {
        LOGGER.info("Saving edited class.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao.updatePupilClass(pupilClass, user.getDbName());
        LOGGER.info("Redirect to list of classes.");
        return new ModelAndView("redirect:/classes?page=1");
    }

    /**
     * Delete class by id.
     * @param id class id
     * @return ModelAndView
     */
    @RequestMapping(value = "/class/{id}/delete", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView deleteClass(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting class " + id + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        PupilClass pupilClass = dao.getPupilClass(id, dbName);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deletePupilClass(id, dbName);
        LOGGER.info("Redirect to list of classes on page " + pageNum + ".");
        return new ModelAndView("redirect:/classes?page=" + pageNum);
    }

    @RequestMapping(value = "classes/search")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    @ResponseBody
    public List<PupilClass> searchPupilClasses(@RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching classes by " + param + ".");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        List<PupilClass> list;
        if(!val.isEmpty()) {
            list = dao.searchPupilClasses(val, param, dbName);
        } else {
            list = dao.getPupilClassesByPage(1, pupilClassPerPage, dbName);
        }
        return list;
    }

    private Map<String, String> getBasicCrumbsMap() {
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Класи", CLASSES_LINK);
        return crumbsMap;
    }
}

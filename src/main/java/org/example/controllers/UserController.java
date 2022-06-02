package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.*;
import org.example.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private final UserDAO dao;
    private final RoleDAO roleDAO;
    private final TeacherDAO teacherDAO;
    private final PupilDAO pupilDAO;
    private final PupilClassDAO pupilClassDAO;
    private final SchoolDAO schoolDAO;
    private static final int userPerPage = 25;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private static final String USERS_LINK = "/users?page=1";

    public UserController(UserDAO dao, RoleDAO roleDAO, TeacherDAO teacherDAO,
                          PupilDAO pupilDAO, PupilClassDAO pupilClassDAO, SchoolDAO schoolDAO, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.roleDAO = roleDAO;
        this.teacherDAO = teacherDAO;
        this.pupilDAO = pupilDAO;
        this.pupilClassDAO = pupilClassDAO;
        this.schoolDAO = schoolDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @InitBinder("teacher")
    public void initBinder1(WebDataBinder binder){
        binder.setFieldDefaultPrefix("teacher.");
    }

    @InitBinder("user")
    public void initBinder2(WebDataBinder binder){
        binder.setFieldDefaultPrefix("user.");
    }

    @InitBinder("pupil")
    public void initBinder3(WebDataBinder binder){
        binder.setFieldDefaultPrefix("pupil.");
    }


    /**
     * Getting page to view all user list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/users")
    @Secured("ADMIN")
    public ModelAndView viewAllUsers(@RequestParam("page") int page) {
        LOGGER.info("Getting list of users for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        int count = dao.getCountOfUsers(dbName);
        PaginationController paginationController = new PaginationController(count, userPerPage, page);
        List<User> list;
        if(count <= userPerPage) {
            list = dao.getAllUsers(dbName);
        } else {
            list = dao.getUsersByPage(page, userPerPage, dbName);
        }
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(getBasicCrumbsMap()));
        model.put("roles", roleDAO.getAllRoles(dbName));
        model.put("list", list);
        model.put("pagination", paginationController);
        LOGGER.info("Printing users list.");
        return new ModelAndView("viewUserList", model);
    }

    /**
     * Getting page for user adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/user")
    @Secured("ADMIN")
    public ModelAndView addUser() {
        LOGGER.info("Add new user.");
        LOGGER.info("Form a model.");
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = currUser.getDbName();
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Додати користувача", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("teacher", new Teacher());
        model.put("pupil", new Pupil());
        model.put("user", new User());
        model.put("method", "post");
        model.put("roles", roleDAO.getAllRoles(dbName));
        model.put("action", "user");
        model.put("list", pupilClassDAO.getAllPupilClasses(dbName));
        return new ModelAndView("userPage", model);
    }

    /**
     * Saving added user.
     * @param user added user
     * @return ModelAndView
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView saveAddedUser(@ModelAttribute User user,
                                       @ModelAttribute Pupil pupil,
                                       @ModelAttribute Teacher teacher,
                                       @RequestParam(value = "ADMIN", required = false) String adminRole,
                                       @RequestParam(value = "PUPIL", required = false) String pupilRole,
                                       @RequestParam(value = "TEACHER", required = false) String teacherRole) throws Exception {
        LOGGER.info("Saving added user.");
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = currUser.getDbName();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.addUser(user, dbName);
        int id = dao.getUserByUsername(user.getUsername(), dbName).getId();
        if (adminRole != null) {
            dao.addUserRole(id, Integer.parseInt(adminRole), dbName);
        }
        if (pupilRole != null) {
            dao.addUserRole(id, Integer.parseInt(pupilRole), dbName);
            pupil.setId(id);
            pupilDAO.addPupil(pupil, dbName);
        }
        if (teacherRole != null) {
            dao.addUserRole(id, Integer.parseInt(teacherRole), dbName);
            teacher.setId(id);
            teacherDAO.addTeacher(teacher, dbName);
        }
        LOGGER.info("Redirect to user list.");
        return new ModelAndView("redirect:" + USERS_LINK);
    }

    /**
     * Saving edited user.
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView saveEditedUser(@ModelAttribute User user,
                                       @ModelAttribute Pupil pupil,
                                       @ModelAttribute Teacher teacher,
                                       @RequestParam(value = "ADMIN", required = false) String adminRole,
                                       @RequestParam(value = "PUPIL", required = false) String pupilRole,
                                       @RequestParam(value = "TEACHER", required = false) String teacherRole) throws Exception {
        LOGGER.info("Saving edited user.");
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = currUser.getDbName();
        if (!currUser.hasRole("ADMIN") && user.getId() != currUser.getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        User oldUser = dao.getUserByID(user.getId(), dbName);
        if (user.getPassword().equals("")) {
            user.setPassword(oldUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        dao.updateUser(user, dbName);
        if (currUser.hasRole("ADMIN")) {
            int id = user.getId();
            if (adminRole != null && !oldUser.hasRole("ADMIN")) {
                dao.addUserRole(id, Integer.parseInt(adminRole), dbName);
            } else if (adminRole == null && oldUser.hasRole("ADMIN")) {
                dao.deleteUserRole(id, 1, dbName);
            }
            if (pupilRole != null && !oldUser.hasRole("PUPIL")) {
                dao.addUserRole(id, Integer.parseInt(pupilRole), dbName);
                pupilDAO.addPupil(pupil, dbName);
            } else if (pupilRole == null && oldUser.hasRole("PUPIL")) {
                dao.deleteUserRole(id, 3, dbName);
                pupilDAO.deletePupil(id, dbName);
            } else if (pupilRole != null && oldUser.hasRole("PUPIL")) {
                pupilDAO.updatePupil(pupil, dbName);
            }
            if (teacherRole != null && !oldUser.hasRole("TEACHER")) {
                dao.addUserRole(id, Integer.parseInt(teacherRole), dbName);
                teacherDAO.addTeacher(teacher, dbName);
            } else if (teacherRole == null && oldUser.hasRole("TEACHER")) {
                dao.deleteUserRole(id, 2, dbName);
                teacherDAO.deleteTeacher(id, dbName);
            } else if (teacherRole != null && oldUser.hasRole("TEACHER")) {
                teacherDAO.updateTeacher(teacher, dbName);
            }
            LOGGER.info("Redirect to user list.");
            return new ModelAndView("redirect:.." + USERS_LINK);
        } else {
            LOGGER.info("Redirect to user page.");
            return new ModelAndView("redirect:/user/" + user.getId());
        }
    }

    /**
     * Delete user by id.
     * @param id user's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/user/{id}/delete", method = RequestMethod.POST)
    @Secured("ADMIN")
    public ModelAndView deleteUser(@PathVariable int id, @RequestParam("page") int pageNum) {
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = currUser.getDbName();
        if (id == 0) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Deleting user " + id + ".");
        User user = dao.getUserByID(id, dbName);
        if (user == null) {
            LOGGER.error("User " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteUser(id, dbName);
        LOGGER.info("Redirect to user list on page " + pageNum + ".");
        return new ModelAndView("redirect:/users?page=" + pageNum);
    }

    @RequestMapping(value = "/checkUsername", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    @ResponseBody
    public String checkUsername(@RequestParam("val") String name, @RequestParam("id") int id, @RequestParam("dbname") String dbName){
        if (dbName.equals("null")) {
            User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dbName = currUser.getDbName();
        }
        User user = dao.getUserByUsername(name, dbName);
        String response = "";
        if(user != null) {
            if (user.getId() != id) {
                response = "Ім'я користувача зайняте!";
            }
        }
        return response;
    }

    @RequestMapping(value = "user/search")
    @Secured("ADMIN")
    @ResponseBody
    public List<User> searchUsers(@RequestParam("val") String val,
                              @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching users by " + param + ".");
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = currUser.getDbName();
        List<User> list;
        if(!val.isEmpty()) {
            list = dao.searchUsers(val, param, dbName);
        } else {
            list = dao.getUsersByPage(1, userPerPage, dbName);
        }
        return list;
    }

    @RequestMapping(value = "/user/{id}")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView getUserPage(@PathVariable int id) {
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = currUser.getDbName();
        User user = dao.getUserByID(id, dbName);
        Map<String, Object> model = new HashMap<>();
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        if (!currUser.hasRole("ADMIN") && user.getId() != currUser.getId()) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        Teacher teacher = teacherDAO.getTeacher(id, dbName);
        if (teacher == null) {
            teacher = new Teacher();
        }
        Pupil pupil = pupilDAO.getPupil(id, dbName);
        if (pupil == null) {
            pupil = new Pupil();
        }
        Map<String, String> crumbsMap = getBasicCrumbsMap();
        crumbsMap.put("Сторінка користувача", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("teacher", teacher);
        model.put("pupil", pupil);
        model.put("user", user);
        model.put("roles", roleDAO.getAllRoles(dbName));
        model.put("action", "../user/" + id);
        if (currUser.hasRole("ADMIN")) {
            model.put("list", pupilClassDAO.getAllPupilClasses(dbName));
        }
        return new ModelAndView("userPage", model);
    }

    @RequestMapping(value = "/registration")
    public ModelAndView getSignUpPage() {
        Map<String, Object> model = new HashMap<>();
        List<School> schools = schoolDAO.getAllSchools();
        model.put("list", schools);
        model.put("command", new User());
        return new ModelAndView("signUp", model);
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView register(@ModelAttribute User user, HttpServletRequest request) throws Exception {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        String dbName = user.getDbName();
        dao.addUser(user, dbName);
        String usernameDBName = String.format("%s%s%s", user.getUsername().trim(),
                String.valueOf(Character.LINE_SEPARATOR), dbName);
        try {
            request.login(usernameDBName, password);
        } catch (ServletException e) {
            LOGGER.error("Error while login ", e);
        }
        return new ModelAndView("redirect:/main");
    }

    private Map<String, String> getBasicCrumbsMap() {
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Користувачі", USERS_LINK);
        return crumbsMap;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLoginPage() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            Map<String, Object> model = new HashMap<>();
            List<School> schools = schoolDAO.getAllSchools();
            model.put("list", schools);
            return new ModelAndView("login", model);
        } else {
            return new ModelAndView("redirect:/main");
        }
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public ModelAndView saveAdmin(@ModelAttribute User user, HttpServletRequest request) throws Exception {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        String dbName = user.getDbName();
        dao.addUser(user, dbName);
        user = dao.getUserByUsername(user.getUsername(), dbName);
        dao.addUserRole(user.getId(), 1, dbName);
        String usernameDBName = String.format("%s%s%s", user.getUsername().trim(),
                String.valueOf(Character.LINE_SEPARATOR), dbName);
        try {
            request.login(usernameDBName, password);
        } catch (ServletException e) {
            LOGGER.error("Error while login ", e);
        }
        return new ModelAndView("redirect:/main");
    }
}

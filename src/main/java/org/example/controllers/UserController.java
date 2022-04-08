package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.*;
import org.example.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private final UserDAO dao;
    private final RoleDAO roleDAO;
    private final TeacherDAO teacherDAO;
    private final PupilDAO pupilDAO;
    private final PupilClassDAO pupilClassDAO;
    private static final int userPerPage = 25;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    public UserController(UserDAO dao, RoleDAO roleDAO, TeacherDAO teacherDAO,
                          PupilDAO pupilDAO, PupilClassDAO pupilClassDAO, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.roleDAO = roleDAO;
        this.teacherDAO = teacherDAO;
        this.pupilDAO = pupilDAO;
        this.pupilClassDAO = pupilClassDAO;
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
    public ModelAndView viewAllUsers(@RequestParam("page") int page) {
        LOGGER.info("Getting list of users for " + page + " page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfUsers();
        PaginationController paginationController = new PaginationController(count, userPerPage, page);
        List<User> list;
        if(count <= userPerPage) {
            list = dao.getAllUsers();
        } else {
            list = dao.getUsersByPage(page, userPerPage);
        }
        model.put("roles", roleDAO.getAllRoles());
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
    public ModelAndView addUser() {
        LOGGER.info("Add new user.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = new User();
        Pupil pupil = new Pupil();
        Teacher teacher = new Teacher();
        model.put("teacher", teacher);
        model.put("pupil", pupil);
        model.put("user", user);
        model.put("method", "post");
        model.put("roles", roleDAO.getAllRoles());
        model.put("action", "user");
        model.put("list", pupilClassDAO.getAllPupilClasses());
        return new ModelAndView("userPage", model);
    }

    /**
     * Saving added user.
     * @param user added user
     * @return ModelAndView
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ModelAndView saveAddedUser(@ModelAttribute User user,
                                       @ModelAttribute Pupil pupil,
                                       @ModelAttribute Teacher teacher,
                                       @RequestParam(value = "ADMIN", required = false) String adminRole,
                                       @RequestParam(value = "PUPIL", required = false) String pupilRole,
                                       @RequestParam(value = "TEACHER", required = false) String teacherRole) throws Exception {
        LOGGER.info("Saving added user.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.addUser(user);
        int id = dao.getUserByUsername(user.getUsername()).getId();
        if (adminRole != null) {
            dao.addUserRole(id, Integer.parseInt(adminRole));
        }
        if (pupilRole != null) {
            dao.addUserRole(id, Integer.parseInt(pupilRole));
            pupil.setId(id);
            pupilDAO.addPupil(pupil);
        }
        if (teacherRole != null) {
            dao.addUserRole(id, Integer.parseInt(teacherRole));
            teacher.setId(id);
            teacherDAO.addTeacher(teacher);
        }
        LOGGER.info("Redirect to user list.");
        return new ModelAndView("redirect:/users?page=1");
    }

    /**
     * Saving edited user.
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
    public ModelAndView saveEditedUser(@ModelAttribute User user,
                                       @ModelAttribute Pupil pupil,
                                       @ModelAttribute Teacher teacher,
                                       @RequestParam(value = "ADMIN", required = false) String adminRole,
                                       @RequestParam(value = "PUPIL", required = false) String pupilRole,
                                       @RequestParam(value = "TEACHER", required = false) String teacherRole) throws Exception {
        LOGGER.info("Saving edited user.");
        User oldUser = dao.getUserByID(user.getId());
        dao.updateUser(user);
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currUser.hasRole("ADMIN")) {
            int id = user.getId();
            if (adminRole != null && !oldUser.hasRole("ADMIN")) {
                dao.addUserRole(id, Integer.parseInt(adminRole));
            } else if (adminRole == null && oldUser.hasRole("ADMIN")) {
                dao.deleteUserRole(id, 1);
            }
            if (pupilRole != null && !oldUser.hasRole("PUPIL")) {
                dao.addUserRole(id, Integer.parseInt(pupilRole));
                pupilDAO.addPupil(pupil);
            } else if (pupilRole == null && oldUser.hasRole("PUPIL")) {
                dao.deleteUserRole(id, 3);
                pupilDAO.deletePupil(id);
            } else if (pupilRole != null && oldUser.hasRole("PUPIL")) {
                pupilDAO.updatePupil(pupil);
            }
            if (teacherRole != null && !oldUser.hasRole("TEACHER")) {
                dao.addUserRole(id, Integer.parseInt(teacherRole));
                teacherDAO.addTeacher(teacher);
            } else if (teacherRole == null && oldUser.hasRole("TEACHER")) {
                dao.deleteUserRole(id, 2);
                teacherDAO.deleteTeacher(id);
            } else if (pupilRole != null && oldUser.hasRole("TEACHER")) {
                teacherDAO.updateTeacher(teacher);
            }
            LOGGER.info("Redirect to user list.");
            return new ModelAndView("redirect:../viewAllUsers?page=1");
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
    @RequestMapping(value = "/user/{id}/delete", method = RequestMethod.GET)
    public ModelAndView deleteUser(@PathVariable int id, @RequestParam("page") int pageNum) {
        if (id == 0) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Deleting user " + id + ".");
        User user = dao.getUserByID(id);
        if (user == null) {
            LOGGER.error("User " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteUser(id);
        LOGGER.info("Redirect to user list on page " + pageNum + ".");
        return new ModelAndView("redirect:/users?page=" + pageNum);
    }

    @RequestMapping(value = "/checkUsername", method = RequestMethod.GET)
    @ResponseBody
    public String checkUsername(@RequestParam("val") String name, @RequestParam("id") int id){
        User user = dao.getUserByUsername(name);
        String response = "";
        if(user != null) {
            if (user.getId() != id) {
                response = "Username already taken!";
            }
        }
        return response;
    }

    @RequestMapping(value = "user/search")
    @ResponseBody
    public List<User> searchUsers(@RequestParam("val") String val,
                              @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching users by " + param + ".");
        List<User> list;
        if(!val.isEmpty()) {
            list = dao.searchUsers(val, param);
        } else {
            list = dao.getUsersByPage(1, userPerPage);
        }
        return list;
    }

    @RequestMapping(value = "/user/{id}")
    public ModelAndView getUserPage(@PathVariable int id) {
        User user = dao.getUserByID(id);
        Map<String, Object> model = new HashMap<>();
        Teacher teacher = teacherDAO.getTeacher(id);
        if (teacher == null) {
            teacher = new Teacher();
        }
        Pupil pupil = pupilDAO.getPupil(id);
        if (pupil == null) {
            pupil = new Pupil();
        }
        model.put("teacher", teacher);
        model.put("pupil", pupil);
        model.put("user", user);
        model.put("roles", roleDAO.getAllRoles());
        model.put("toRoot", "../");
        model.put("action", "../user/" + id);
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currUser.hasRole("ADMIN")) {
            model.put("list", pupilClassDAO.getAllPupilClasses());
        }
        return new ModelAndView("userPage", model);
    }
}

package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.PupilDAO;
import org.example.dao.RoleDAO;
import org.example.dao.TeacherDAO;
import org.example.dao.UserDAO;
import org.example.entities.Role;
import org.example.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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
    private static final int userPerPage = 25;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    public UserController(UserDAO dao, RoleDAO roleDAO, TeacherDAO teacherDAO, PupilDAO pupilDAO, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.roleDAO = roleDAO;
        this.teacherDAO = teacherDAO;
        this.pupilDAO = pupilDAO;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Getting page to view all user list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllUsers")
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
    @RequestMapping(value = "/addUser")
    public ModelAndView addUser() {
        LOGGER.info("Add new user.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User user = new User();
        model.put("command", user);
        model.put("title", "Add user");
        model.put("formAction", "saveAddedUser");
        LOGGER.info("Printing form for input user data.");
        return new ModelAndView("userForm", model);
    }

    /**
     * Saving added user.
     * @param user added user
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedUser", method = RequestMethod.POST)
    public ModelAndView saveAddedUser(@ModelAttribute User user) throws Exception {
        LOGGER.info("Saving added user.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.addUser(user);
        LOGGER.info("Redirect to user list.");
        return new ModelAndView("redirect:/viewAllUsers?page=1");
    }

    /**
     * Getting page for user editing.
     * @param id user's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editUser/{id}")
    public ModelAndView editUser(@PathVariable int id) {
        LOGGER.info("Edit user.");
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = dao.getUserByID(id);
        if (user == null) {
            LOGGER.error("User " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        if (currUser.hasRole("ADMIN") || currUser.equals(user)) {
            LOGGER.info("Form a model.");
            Map<String, Object> model = new HashMap<>();
            user.setPassword("");
            model.put("command", user);
            model.put("title", "Edit user");
            model.put("formAction", "../saveEditedUser");
            model.put("toRoot", "../");
            LOGGER.info("Printing form for changing user data.");
            return new ModelAndView("userForm", model);
        } else {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Saving edited user.
     * @param user edited user
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedUser", method = RequestMethod.POST)
    public ModelAndView saveEditedUser(@ModelAttribute User user) throws Exception {
        LOGGER.info("Saving edited user.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.updateUser(user);
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currUser.hasRole("ADMIN")) {
            LOGGER.info("Redirect to user list.");
            return new ModelAndView("redirect:/viewAllUsers?page=1");
        } else {
            LOGGER.info("Redirect to user page.");
            return new ModelAndView("redirect:/userPage ");
        }
    }

    /**
     * Delete user by id.
     * @param id user's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteUser/{id}")
    public ModelAndView deleteUser(@PathVariable int id, @RequestParam("page") int pageNum) {
        if (id == 1) {
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
        return new ModelAndView("redirect:/viewAllUsers?page=" + pageNum);
    }

    /**
     * Add user's role.
     * @param userID user's id
     * @param roleID role's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/addRole/{userID}/{roleID}")
    public ModelAndView addRole(@PathVariable int userID, @PathVariable int roleID) {
        LOGGER.info("Add role " + roleID + " to user " + userID + ".");
        User user = dao.getUserByID(userID);
        if (user == null) {
            LOGGER.error("User " + userID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Role role = roleDAO.getRoleByID(roleID);
        if (role == null) {
            LOGGER.error("Role " + roleID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.addUserRole(userID, roleID);
        LOGGER.info("Redirect to user list.");
        return new ModelAndView("redirect:/viewAllUsers?page=1");
    }

    /**
     * Delete user's role.
     * @param userID user's id
     * @param roleID role's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteRole/{userID}/{roleID}")
    public ModelAndView deleteRole(@PathVariable int userID, @PathVariable int roleID) {
        if (userID == 1 && roleID == 1) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Deleting role " + roleID + " from user " + userID + ".");
        User user = dao.getUserByID(userID);
        if (user == null) {
            LOGGER.error("User " + userID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Role role = roleDAO.getRoleByID(roleID);
        if (role == null) {
            LOGGER.error("Role " + roleID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteUserRole(userID, roleID);
        LOGGER.info("Redirect to user list.");
        return new ModelAndView("redirect:/viewAllUsers?page=1");
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

    @RequestMapping(value = "/searchUsers")
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

    @RequestMapping(value = "/userPage")
    public ModelAndView getUserPage() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> model = new HashMap<>();
        if (user.hasRole("TEACHER")) {
            model.put("teacher", teacherDAO.getTeacher(user.getId()));
        } else if (user.hasRole("PUPIL")){
            model.put("pupil", pupilDAO.getPupil(user.getId()));
        }
        return new ModelAndView("userPage", model);
    }
}

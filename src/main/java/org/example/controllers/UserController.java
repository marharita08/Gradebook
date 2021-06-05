package org.example.controllers;

import org.example.dao.OracleRoleDAO;
import org.example.dao.OracleUserDAO;
import org.example.entities.Role;
import org.example.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class UserController {
    private OracleUserDAO dao;
    private OracleRoleDAO roleDAO;
    private int userPerPage = 10;
    private final PasswordEncoder passwordEncoder;

    public UserController(OracleUserDAO dao, OracleRoleDAO roleDAO, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Getting page to view all user list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllUsers")
    public ModelAndView viewAllUsers(@RequestParam("page") int page) {
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
        return new ModelAndView("viewUserList", model);
    }

    /**
     * Getting page for user adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addUser")
    public ModelAndView addUser() {
        Map<String, Object> model = new HashMap<>();
        User user = new User();
        model.put("command", user);
        model.put("title", "Add user");
        model.put("formAction", "saveAddedUser");
        return new ModelAndView("userForm", model);
    }

    /**
     * Saving added user.
     * @param user added user
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedUser", method = RequestMethod.POST)
    public ModelAndView saveAddedUser(@ModelAttribute User user) throws Exception {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.addUser(user);
        return new ModelAndView("redirect:/viewAllUsers?page=1");
    }

    /**
     * Getting page for user editing.
     * @param id user's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editUser/{id}")
    public ModelAndView editUser(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        User user = dao.getUserByID(id);
        user.setPassword("");
        model.put("command", user);
        model.put("title", "Edit user");
        model.put("formAction", "../saveEditedUser");
        return new ModelAndView("userForm", model);
    }

    /**
     * Saving edited user.
     * @param user edited user
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedUser", method = RequestMethod.POST)
    public ModelAndView saveEditedUser(@ModelAttribute User user) throws Exception {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.updateUser(user);
        return new ModelAndView("redirect:/viewAllUsers?page=1");
    }

    /**
     * Delete user by id.
     * @param id user's id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteUser/{id}")
    public ModelAndView deleteUser(@PathVariable int id, @RequestParam("page") int pageNum) {
        dao.deleteUser(id);
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
        dao.addUserRole(userID, roleID);
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
        dao.deleteUserRole(userID, roleID);
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
    public String searchUsers(@RequestParam("page") int pageNum,
                              @RequestParam("val") String val,
                              @RequestParam("param")String param) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<User> list;
        Set<Role> roles = roleDAO.getAllRoles();
        if(!val.isEmpty()) {
            list = dao.searchUsers(val, param);
        } else {
            list = dao.getUsersByPage(pageNum, userPerPage);
        }
        for (User user:list) {
            sb.append("<tr>");
            sb.append("<td>").append(user.getId()).append("</td>");
            sb.append("<td>").append(user.getUsername()).append("</td>");
            sb.append("<td>").append(user.getPassword()).append("</td>");
            sb.append("<td>");
            for (Role role:user.getRoles()) {
                sb.append("<p>");
                sb.append(role.getName()).append(" ");
                if(role.getId()!=1 || user.getId()!=1) {
                    sb.append("<a href=\"deleteRole/").append(user.getId())
                            .append("/").append(role.getId()).append("\">Delete</a>");
                }
                sb.append("</p>");
            }
            sb.append("</td>").append("<td>");
            for (Role role:roles) {
                sb.append("<p><a href=\"addRole/").append(user.getId()).append("/")
                        .append(role.getId()).append("\">").append(role.getName()).append("</a></p>");
            }
            sb.append("</td>").append("<td>");
            sb.append("<a href=\"editUser/").append(user.getId()).append("\">Edit</a>");
            sb.append("</td>").append("<td>");
            if (user.getId() != 1) {
                sb.append("<a href=\"deleteUser/").append(user.getId()).append("?page=").append(pageNum)
                        .append("\">Delete</a>");
            }
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }
}

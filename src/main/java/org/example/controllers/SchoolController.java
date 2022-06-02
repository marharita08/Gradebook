package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.SchoolDAO;
import org.example.entities.School;
import org.example.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SchoolController {
    private final SchoolDAO dao;
    private static final Logger LOGGER = Logger.getLogger(SchoolController.class.getName());

    public SchoolController(SchoolDAO dao) {
        this.dao = dao;
    }

    /**
     * Getting main page.
     * @return ModelAndView
     */
    @RequestMapping(value = "/main")
    public ModelAndView mainPage() {
        LOGGER.info("Get main page.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = currUser.getDbName();
        School school = dao.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        LOGGER.info("Printing main page.");
        return new ModelAndView("main", model);
    }

    /**
     * Getting page for school adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/school")
    public ModelAndView addSchool() {
        LOGGER.info("Add new school.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new School());
        model.put("title", "Додати школу");
        LOGGER.info("Printing form for input school data.");
        return new ModelAndView("schoolForm", model);
    }

    /**
     * Saving added school.
     * @param school added school
     * @return ModelAndView
     */
    @RequestMapping(value = "/school", method = RequestMethod.POST)
    public ModelAndView saveAddedSchool(@ModelAttribute School school) {
        LOGGER.info("Saving added school.");
        dao.addSchool(school);
        school = dao.getSchoolByName(school.getName());
        String strID = String.valueOf(school.getId());
        dao.createDB(strID);
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "java", "weblogic.WLST", "gradebook/createDataSource.py", strID);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dao.initTables(strID);
        Map<String, Object> model = new HashMap<>();
        User user = new User();
        user.setDbName(strID);
        model.put("command", user);
        model.put("school", school);
        LOGGER.info("Redirect to admin page.");
        return new ModelAndView("addSuperAdminForm", model);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView redirectToMainPage() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            return new ModelAndView("redirect:/login");
        } else {
            return new ModelAndView("redirect:/main");
        }
    }


}

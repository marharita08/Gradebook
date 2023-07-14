package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.SchoolDAO;
import org.example.entities.School;
import org.example.entities.User;
import org.example.services.FileUploadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.File;
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
        model.put("action", "school");
        model.put("title", "Додати школу");
        LOGGER.info("Printing form for input school data.");
        return new ModelAndView("schoolForm", model);
    }

    /**
     * Saving added school.
     * @param school added school
     * @return ModelAndView
     */
    @RequestMapping(value = "/school", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView saveAddedSchool(@ModelAttribute School school, @RequestParam(required = false) MultipartFile file) {
        LOGGER.info("Saving added school.");
        dao.addSchool(school);
        school = dao.getSchoolByName(school.getName());
        String strID = String.valueOf(school.getId());
        if (file != null) {
            String uploadDir = "public/schools/" + strID;
            String[] arr = file.getOriginalFilename().split("\\.");
            String ext = arr[arr.length-1];
            String fileName = strID + "." + ext;
            school.setPhoto(uploadDir + "/" + fileName);
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            dao.updateSchool(school);
        }
        dao.createDB(strID);
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "java", "weblogic.WLST", "createDataSource.py", strID);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                LOGGER.info(line);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
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

    /**
     * Getting page for school editing.
     * @return ModelAndView
     */
    @Secured("ADMIN")
    @RequestMapping(value = "/school/{id}")
    public ModelAndView editSchool(@PathVariable int id) {
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currUser.getDbName().equals(String.valueOf(id))) {
            return new ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Edit school.");
        LOGGER.info("Form a model.");
        School school = dao.getSchool(id);
        Map<String, Object> model = new HashMap<>();
        model.put("command", school);
        model.put("title", "Редагувати школу");
        model.put("action", "../school/" + id);
        LOGGER.info("Printing form for input school data.");
        return new ModelAndView("schoolForm", model);
    }

    /**
     * Saving edited school.
     * @param school edited school
     * @return ModelAndView
     */
    @Secured("ADMIN")
    @RequestMapping(value = "/school/{id}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView saveEditedSchool(@ModelAttribute School school, @RequestParam(required = false) MultipartFile file) {
        LOGGER.info("Saving edited school.");
        String strID = String.valueOf(school.getId());
        if (file != null) {
            if (school.getPhoto() != null) {
                File myObj = new File(school.getPhoto());
                if (myObj.delete()) {
                    LOGGER.info("Deleted the file: " + myObj.getName());
                } else {
                    LOGGER.error("Failed to delete the file.");
                }
            }
            String uploadDir = "public/schools/" + strID;
            String[] arr = file.getOriginalFilename().split("\\.");
            String ext = arr[arr.length-1];
            String fileName = strID + "." + ext;
            school.setPhoto(uploadDir + "/" + fileName);
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        dao.updateSchool(school);
        LOGGER.info("Redirect to main page.");
        return new ModelAndView("redirect:../main");
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

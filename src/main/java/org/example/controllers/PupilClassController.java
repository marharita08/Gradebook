package org.example.controllers;

import org.example.dao.OraclePupilClassDAO;
import org.example.dao.OracleSubjectDAO;
import org.example.dao.OracleUserDAO;
import org.example.entities.Pupil;
import org.example.entities.PupilClass;
import org.example.entities.Teacher;
import org.example.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PupilClassController {
    private OraclePupilClassDAO dao;
    private OracleSubjectDAO subjectDAO;
    private OracleUserDAO userDAO;
    private int pupilClassPerPage = 15;

    public PupilClassController(OraclePupilClassDAO dao,
                                OracleSubjectDAO subjectDAO,
                                OracleUserDAO userDAO) {
        this.dao = dao;
        this.subjectDAO = subjectDAO;
        this.userDAO = userDAO;
    }

    /**
     * Getting page to view all classes list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllClasses")
    public ModelAndView viewAllClasses(@RequestParam("page") int page) {
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfPupilClasses();
        PaginationController paginationController = new PaginationController(count, pupilClassPerPage, page);
        List<PupilClass> list;
        if(count <= pupilClassPerPage) {
            list = dao.getAllPupilClasses();
        } else {
            list = dao.getPupilClassesByPage(page, pupilClassPerPage);
        }
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("/Gradebook/viewAllClasses"));
        model.put("header", "All classes");
        model.put("pageNum", page);
        return new ModelAndView("viewClassList", model);
    }

    /**
     * Get page to view classes which learn subject with set id.
     * @param id subject id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewPupilClassesBySubject/{id}")
    public ModelAndView viewPupilClassesBySubject(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        model.put("list", dao.getPupilClassesBySubject(id));
        model.put("header", "Classes which learn " + subjectDAO.getSubject(id).getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        return new ModelAndView("viewClassList", model);
    }

    /**
     * Getting page for class adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addClass")
    public ModelAndView addClass() {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new PupilClass());
        model.put("selectedGrade", 1);
        model.put("title", "Add class");
        model.put("formAction", "saveAddedClass");
        return new ModelAndView("classForm", model);
    }

    /**
     * Saving added class.
     * @param pupilClass added class
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedClass", method = RequestMethod.POST)
    public ModelAndView saveAddedClass(@ModelAttribute PupilClass pupilClass) {
        dao.addPupilClass(pupilClass);
        return new ModelAndView("redirect:/viewAllClasses?page=1");
    }

    /**
     * Getting page for class editing.
     * @param id class id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editClass/{id}")
    public ModelAndView editClass(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        PupilClass pupilClass = dao.getPupilClass(id);
        model.put("command", pupilClass);
        model.put("selectedGrade", pupilClass.getGrade());
        model.put("title", "Edit class");
        model.put("formAction", "../saveEditedClass");
        return new ModelAndView("classForm", model);
    }

    /**
     * Saving edited class.
     * @param pupilClass edited class
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedClass", method = RequestMethod.POST)
    public ModelAndView saveEditedClass(@ModelAttribute PupilClass pupilClass) {
        dao.updatePupilClass(pupilClass);
        return new ModelAndView("redirect:/viewAllClasses?page=1");
    }

    /**
     * Delete class by id.
     * @param id class id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteClass/{id}")
    public ModelAndView deleteClass(@PathVariable int id, @RequestParam("page") int pageNum) {
        dao.deletePupilClass(id);
        return new ModelAndView("redirect:/viewAllClasses?page=" + pageNum);
    }

    @RequestMapping(value = "/searchPupilClasses")
    @ResponseBody
    public String searchPupilClasses(@RequestParam("page") int pageNum,
                                 @RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<PupilClass> list;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDAO.getUserByUsername(username);
        if(!val.isEmpty()) {
            list = dao.searchPupilClasses(val, param);
        } else {
            list = dao.getPupilClassesByPage(pageNum, pupilClassPerPage);
        }
        for (PupilClass pupilClass:list) {
            int id = pupilClass.getId();
            sb.append("<tr>");
            if (user.hasRole("ADMIN")) {
                sb.append("<td>").append(id).append("</td>");
                sb.append("<td>").append(pupilClass.getGrade()).append("</td>");
            }
            sb.append("<td>").append(pupilClass.getName()).append("</td>");
            if (user.hasRole("ADMIN")) {
                sb.append("<td>");
                sb.append("<a href=\"editClass/").append(id).append("\">Edit</a>");
                sb.append("</td>").append("<td>");
                sb.append("<a href=\"deleteClass/").append(id).append("?page=").append(pageNum).append("\">Delete</a></td>");
                sb.append("</td>");
            }
            sb.append("<td>");
            sb.append("<a href=\"/Gradebook/viewPupilsByPupilClass/").append(id).append("\">view pupil list</a>");
            sb.append("</td>").append("<td>");
            sb.append("<a href=\"/Gradebook/viewSubjectsByPupilClass/").append(id).append("\">view subjects</a>");
            sb.append("</td>");
            sb.append("<td>");
            sb.append("<a href=\"/Gradebook/viewTeachersByPupilClass/").append(id).append("\">view teacher list</a>");
            sb.append("</td>").append("<td>");
            sb.append("<a href=\"/Gradebook/viewSubjectDetailsByPupilClass/").append(id).append("\">view teacher-subject list</a>");
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }
}

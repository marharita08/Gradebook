package org.example.controllers;

import org.example.dao.*;
import org.example.entities.SubjectDetails;
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
public class SubjectDetailsController {
    private OracleSubjectDetailsDAO dao;
    private OraclePupilClassDAO classDAO;
    private OracleTeacherDAO teacherDAO;
    private OracleSubjectDAO subjectDAO;
    private OracleUserDAO userDAO;
    private int subjectDetailsPerPage = 15;

    public SubjectDetailsController(OracleSubjectDetailsDAO dao,
                                    OraclePupilClassDAO classDAO,
                                    OracleTeacherDAO teacherDAO,
                                    OracleSubjectDAO subjectDAO,
                                    OracleUserDAO userDAO) {
        this.dao = dao;
        this.classDAO = classDAO;
        this.teacherDAO = teacherDAO;
        this.subjectDAO = subjectDAO;
        this.userDAO = userDAO;
    }

    /**
     * Getting page to view all subject details list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewAllSubjectDetails")
    public ModelAndView viewAllSubjectDetails(@RequestParam("page") int page) {
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfSubjectDetails();
        PaginationController paginationController = new PaginationController(count, subjectDetailsPerPage, page);
        List<SubjectDetails> list;
        if(count <= subjectDetailsPerPage) {
            list = dao.getAllSubjectDetails();
        } else {
            list = dao.getSubjectDetailsByPage(page, subjectDetailsPerPage);
        }
        model.put("list", list);
        model.put("pagination", paginationController.makePagingLinks("/Gradebook/viewAllSubjectDetails"));
        model.put("header", "Subject Details List");
        model.put("pageNum", page);
        model.put("param", "all");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page for subject details adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addSubjectDetails")
    public ModelAndView addSubjectDetails() {
        Map<String, Object> model = new HashMap<>();
        model.put("command", new SubjectDetails());
        model.put("selectedClass", 0);
        model.put("selectedTeacher", 0);
        model.put("selectedSubject", 0);
        model.put("classList", classDAO.getAllPupilClasses());
        model.put("teacherList", teacherDAO.getAllTeachers());
        model.put("subjectList", subjectDAO.getAllSubjects());
        model.put("title", "Add Subject Details");
        model.put("formAction", "saveAddedSubjectDetails");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving added subject details.
     * @param subjectDetails added subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedSubjectDetails", method = RequestMethod.POST)
    public ModelAndView saveAddedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) {
        dao.addSubjectDetails(subjectDetails);
        return new ModelAndView("redirect:/viewAllSubjectDetails?page=1");
    }

    /**
     * Getting page for subject details editing.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editSubjectDetails/{id}", method = RequestMethod.GET)
    public ModelAndView editSubjectDetails(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        SubjectDetails subjectDetails = dao.getSubjectDetails(id);
        model.put("command", subjectDetails);
        model.put("selectedClass", subjectDetails.getPupilClass().getId());
        model.put("selectedTeacher", subjectDetails.getTeacher().getId());
        model.put("selectedSubject", subjectDetails.getSubject().getId());
        model.put("classList", classDAO.getAllPupilClasses());
        model.put("teacherList", teacherDAO.getAllTeachers());
        model.put("subjectList", subjectDAO.getAllSubjects());
        model.put("title", "Edit subject details");
        model.put("formAction", "../saveEditedSubjectDetails");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving edited subject details.
     * @param subjectDetails edited subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedSubjectDetails", method = RequestMethod.POST)
    public ModelAndView saveEditedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) {
        dao.updateSubjectDetails(subjectDetails);
        return new ModelAndView("redirect:/viewAllSubjectDetails?page=1");
    }

    /**
     * Delete subject details by id.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteSubjectDetails/{id}")
    public ModelAndView deleteSubjectDetails(@PathVariable int id, @RequestParam("page") int pageNum) {
        dao.deleteSubjectDetails(id);
        return new ModelAndView("redirect:/viewAllSubjectDetails?page=" + pageNum);
    }

    /**
     * Getting page to view subject details by teacher.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewSubjectDetailsByTeacher/{id}")
    public ModelAndView viewSubjectDetailsByTeacher(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsByTeacher(id);
        model.put("list", list);
        model.put("param", "teacher");
        model.put("header", "Subjects of " + teacherDAO.getTeacher(id).getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by class.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewSubjectDetailsByPupilClass/{id}")
    public ModelAndView viewSubjectDetailsByPupilClass(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsByPupilClass(id);
        model.put("list", list);
        model.put("param", "class");
        model.put("header", "Subjects of " + classDAO.getPupilClass(id).getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by subject.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewSubjectDetailsBySubject/{id}")
    public ModelAndView viewSubjectDetailsBySubject(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsBySubject(id);
        model.put("list", list);
        model.put("param", "subject");
        model.put("header", "Subject " + subjectDAO.getSubject(id).getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    @RequestMapping(value = "/searchSubjectDetails")
    @ResponseBody
    public String searchSubjectDetails(@RequestParam("page") int pageNum,
                                 @RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<SubjectDetails> list;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDAO.getUserByUsername(username);
        if(!val.isEmpty()) {
            list = dao.searchSubjectDetails(val, param);
        } else {
            list = dao.getSubjectDetailsByPage(pageNum, subjectDetailsPerPage);
        }
        for (SubjectDetails subjectDetails:list) {
            int id = subjectDetails.getId();
            sb.append("<tr>");
            if (user.hasRole("ADMIN")) {
                sb.append("<td>").append(id).append("</td>");
            }
            sb.append("<td>").append(subjectDetails.getPupilClass().getName()).append("</td>");
            sb.append("<td>");
            if (subjectDetails.getTeacher() != null) {
                sb.append(subjectDetails.getTeacher().getName());
            } else {
                sb.append("-");
            }
            sb.append("</td>");
            sb.append("<td>").append(subjectDetails.getSubject().getName()).append("</td>");
            if (user.hasRole("ADMIN")) {
                sb.append("<td>");
                sb.append("<a href=\"editSubjectDetails/").append(id).append("\">Edit</a>");
                sb.append("</td>").append("<td>");
                sb.append("<a href=\"deleteSubjectDetails/").append(id).append("?page=").append(pageNum).append("\">Delete</a></td>");
                sb.append("</td>");
            }
            sb.append("<td>");
            sb.append("<a href=\"/Gradebook/viewLessonsBySubjectDetails/").append(id).append("?page=1\">view lessons</a>");
            sb.append("</td>");
            if (user.hasRole("TEACHER")) {
                sb.append("<td>");
                sb.append("<a href=\"/Gradebook/addLesson/").append(id).append("\">add lesson</a>");
                sb.append("</td>");
            }
            sb.append("<td>");
            sb.append("<a href=\"/Gradebook/viewMarksBySubjectDetails/").append(id).append("\">view marks</a>");
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }
}

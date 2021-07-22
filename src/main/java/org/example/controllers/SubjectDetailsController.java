package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.*;
import org.example.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectDetailsController {
    private final SubjectDetailsDAO dao;
    private final PupilClassDAO classDAO;
    private final TeacherDAO teacherDAO;
    private final SubjectDAO subjectDAO;
    private final UserDAO userDAO;
    private int subjectDetailsPerPage = 15;
    private static final Logger LOGGER = Logger.getLogger(SubjectDetailsController.class.getName());

    public SubjectDetailsController(SubjectDetailsDAO dao,
                                    PupilClassDAO classDAO,
                                    TeacherDAO teacherDAO,
                                    SubjectDAO subjectDAO,
                                    UserDAO userDAO) {
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
        LOGGER.info("Getting list of subject details for " + page + " page.");
        LOGGER.info("Form a model.");
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
        model.put("toRoot", "");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page for subject details adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addSubjectDetails")
    public ModelAndView addSubjectDetails() {
        LOGGER.info("Add new subject details.");
        LOGGER.info("Form a model.");
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
        LOGGER.info("Printing form for input subject details data.");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving added subject details.
     * @param subjectDetails added subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedSubjectDetails", method = RequestMethod.POST)
    public ModelAndView saveAddedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) throws Exception {
        LOGGER.info("Saving added subject details.");
        dao.addSubjectDetails(subjectDetails);
        LOGGER.info("Redirect to subject details list.");
        return new ModelAndView("redirect:/viewAllSubjectDetails?page=1");
    }

    /**
     * Getting page for subject details editing.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editSubjectDetails/{id}", method = RequestMethod.GET)
    public ModelAndView editSubjectDetails(@PathVariable int id) {
        LOGGER.info("Edit subject details.");
        SubjectDetails subjectDetails = dao.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details" + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", subjectDetails);
        model.put("selectedClass", subjectDetails.getPupilClass().getId());
        model.put("selectedTeacher", subjectDetails.getTeacher().getId());
        model.put("selectedSubject", subjectDetails.getSubject().getId());
        model.put("classList", classDAO.getAllPupilClasses());
        model.put("teacherList", teacherDAO.getAllTeachers());
        model.put("subjectList", subjectDAO.getAllSubjects());
        model.put("title", "Edit subject details");
        model.put("formAction", "../saveEditedSubjectDetails");
        LOGGER.info("Printing form for changing subject details data.");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving edited subject details.
     * @param subjectDetails edited subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedSubjectDetails", method = RequestMethod.POST)
    public ModelAndView saveEditedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) throws Exception {
        LOGGER.info("Saving edited subject details.");
        dao.updateSubjectDetails(subjectDetails);
        LOGGER.info("Redirect to subject details list.");
        return new ModelAndView("redirect:/viewAllSubjectDetails?page=1");
    }

    /**
     * Delete subject details by id.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteSubjectDetails/{id}")
    public ModelAndView deleteSubjectDetails(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting subject details " + id + ".");
        SubjectDetails subjectDetails = dao.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details" + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSubjectDetails(id);
        LOGGER.info("Redirect to subject details list on page " + pageNum + ".");
        return new ModelAndView("redirect:/viewAllSubjectDetails?page=" + pageNum);
    }

    /**
     * Getting page to view subject details by teacher.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewSubjectDetailsByTeacher/{id}")
    public ModelAndView viewSubjectDetailsByTeacher(@PathVariable int id) {
        LOGGER.info("Getting list of subject details by " + id + " teacher.");
        Teacher teacher = teacherDAO.getTeacher(id);
        if (teacher == null) {
            LOGGER.error("Teacher " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsByTeacher(id);
        model.put("list", list);
        model.put("param", "teacher");
        model.put("header", "Subjects of " + teacher.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by class.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewSubjectDetailsByPupilClass/{id}")
    public ModelAndView viewSubjectDetailsByPupilClass(@PathVariable int id) {
        LOGGER.info("Getting list of subject details by " + id + " class.");
        PupilClass pupilClass = classDAO.getPupilClass(id);
        if (pupilClass == null) {
            LOGGER.error("Class " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsByPupilClass(id);
        model.put("list", list);
        model.put("param", "class");
        model.put("header", "Subjects of " + pupilClass.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by subject.
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewSubjectDetailsBySubject/{id}")
    public ModelAndView viewSubjectDetailsBySubject(@PathVariable int id) {
        LOGGER.info("Getting list of subject details by " + id + " subject.");
        Subject subject = subjectDAO.getSubject(id);
        if (subject == null) {
            LOGGER.error("Subject " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsBySubject(id);
        model.put("list", list);
        model.put("param", "subject");
        model.put("header", "Subject " + subject.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    @RequestMapping(value = "/searchSubjectDetails")
    @ResponseBody
    public String searchSubjectDetails(@RequestParam("page") int pageNum,
                                 @RequestParam("val") String val,
                                 @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching subject details by " + param + ".");
        StringBuilder sb = new StringBuilder();
        List<SubjectDetails> list;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDAO.getUserByUsername(username);
        if(!val.isEmpty()) {
            list = dao.searchSubjectDetails(val, param);
        } else {
            list = dao.getSubjectDetailsByPage(pageNum, subjectDetailsPerPage);
        }
        LOGGER.info("Forming response.");
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
        LOGGER.info("Printing response.");
        return sb.toString();
    }
}

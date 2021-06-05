package org.example.controllers;

import org.example.dao.*;
import org.example.entities.Lesson;
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
public class LessonController {
    private OracleLessonDAO dao;
    private OracleSubjectDetailsDAO subjectDetailsDAO;
    private OracleUserDAO userDAO;
    private int lessonsPerPage = 20;

    public LessonController(OracleLessonDAO dao,
                            OracleSubjectDetailsDAO subjectDetailsDAO,
                            OracleUserDAO userDAO) {
        this.dao = dao;
        this.subjectDetailsDAO = subjectDetailsDAO;
        this.userDAO = userDAO;
    }

    /**
     * Getting page for lesson adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/addLesson/{id}")
    public ModelAndView addLesson(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        model.put("command", new Lesson(subjectDetails));
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Add lesson");
        model.put("formAction", "/Gradebook/saveAddedLesson");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving added lesson.
     * @param lesson added lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveAddedLesson", method = RequestMethod.POST)
    public ModelAndView saveAddedLesson(@ModelAttribute Lesson lesson) {
        dao.addLesson(lesson);
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + lesson.getSubjectDetails().getId() + "?page=1");
    }

    /**
     * Getting page for lesson editing.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/editLesson/{id}")
    public ModelAndView editLesson(@PathVariable int id) {
        Map<String, Object> model = new HashMap<>();
        Lesson lesson = dao.getLesson(id);
        SubjectDetails subjectDetails = lesson.getSubjectDetails();
        model.put("command", lesson);
        model.put("teacher", subjectDetails.getTeacher().getName());
        model.put("subject", subjectDetails.getSubject().getName());
        model.put("class", subjectDetails.getPupilClass().getName());
        model.put("title", "Edit lesson");
        model.put("formAction", "/Gradebook/saveEditedLesson");
        return new ModelAndView("lessonForm", model);
    }

    /**
     * Saving edited lesson.
     * @param lesson edited lesson
     * @return ModelAndView
     */
    @RequestMapping(value = "/saveEditedLesson", method = RequestMethod.POST)
    public ModelAndView saveEditedLesson(@ModelAttribute Lesson lesson) {
        dao.updateLesson(lesson);
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + lesson.getSubjectDetails().getId() + "?page=1");
    }

    /**
     * Delete lesson by id.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/deleteLesson/{id}")
    public ModelAndView deleteLesson(@PathVariable int id, @RequestParam("page") int pageNum) {
        SubjectDetails subjectDetails = dao.getLesson(id).getSubjectDetails();
        dao.deleteLesson(id);
        return new ModelAndView("redirect:/viewLessonsBySubjectDetails/" + subjectDetails.getId() + "?page=" + pageNum);
    }

    /**
     * View lessons by subject details
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/viewLessonsBySubjectDetails/{id}")
    public ModelAndView viewLessonsBySubjectDetails(@PathVariable int id, @RequestParam("page") int page) {
        List<Lesson> list;
        Map<String, Object> model = new HashMap<>();
        int count = dao.getCountOfLessons(id);
        if (count <= lessonsPerPage) {
            list = dao.getLessonsBySubjectDetails(id);
        } else {
            list = dao.getLessonsBySubjectDetailsAndPage(id, page, lessonsPerPage);
        }
        model.put("list", list);
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id);
        model.put("header", "Lessons of "
                    + subjectDetails.getSubject().getName()
                    + " " + subjectDetails.getPupilClass().getName());
        if (subjectDetails.getTeacher() != null) {
            model.put("teacher", "Teacher: " + subjectDetails.getTeacher().getName());
        }
        model.put("subjectDetails", subjectDetails.getId());
        PaginationController paginationController = new PaginationController(count, lessonsPerPage, page);
        model.put("pagination", paginationController.makePagingLinks("/Gradebook/viewLessonsBySubjectDetails/" + subjectDetails.getId()));
        model.put("pageNum", page);
        return new ModelAndView("lessonList", model);
    }

    @RequestMapping(value = "/searchLessons")
    @ResponseBody
    public String searchLessons(@RequestParam("page") int pageNum,
                                 @RequestParam("val") String val,
                                 @RequestParam("param")String param,
                                @RequestParam("sd") int sd) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Lesson> list;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDAO.getUserByUsername(username);
        if(!val.isEmpty()) {
            list = dao.searchLessons(val, param, sd);
        } else {
            list = dao.getLessonsBySubjectDetailsAndPage(sd, pageNum, lessonsPerPage);
        }
        for (Lesson lesson:list) {
            int id = lesson.getId();
            sb.append("<tr>");
            if (user.hasRole("ADMIN")) {
                sb.append("<td>").append(id).append("</td>");
            }
            sb.append("<td>").append(lesson.getDate()).append("</td>");
            sb.append("<td>").append(lesson.getTopic()).append("</td>");
            sb.append("<td>");
            sb.append("<a href=\"/Gradebook/viewMarksByLesson/").append(id).append("\">view marks</a>");
            sb.append("</td>");
            if (user.hasRole("TEACHER")) {
                sb.append("<td>");
                sb.append("<a href=\"/Gradebook/addMark/").append(id).append("\">add mark</a>");
                sb.append("</td>");
                sb.append("<td>");
                sb.append("<a href=\"/Gradebook/editLesson/").append(id).append("\">edit lesson</a>");
                sb.append("</td>").append("<td>");
                sb.append("<a href=\"/Gradebook/deleteLesson/").append(id).append("?page=").append(pageNum).append("\">delete lesson</a>");
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        return sb.toString();
    }
}

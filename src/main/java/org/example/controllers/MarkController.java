package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.*;
import org.example.entities.*;
import org.example.services.MarkService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class MarkController {
    private final MarkDAO dao;
    private final SchoolDAO schoolDAO;
    private final LessonDAO lessonDAO;
    private final PupilDAO pupilDAO;
    private final SubjectDAO subjectDAO;
    private final SubjectDetailsDAO subjectDetailsDAO;
    private final MarkService markService;
    private static final int marksPerPage = 12;
    private static final Logger LOGGER = Logger.getLogger(MarkController.class.getName());

    public MarkController(MarkDAO dao,
                          SchoolDAO schoolDAO, LessonDAO lessonDAO,
                          PupilDAO pupilDAO,
                          SubjectDAO subjectDAO,
                          SubjectDetailsDAO subjectDetailsDAO, MarkService markService) {
        this.dao = dao;
        this.schoolDAO = schoolDAO;
        this.lessonDAO = lessonDAO;
        this.pupilDAO = pupilDAO;
        this.subjectDAO = subjectDAO;
        this.subjectDetailsDAO = subjectDetailsDAO;
        this.markService = markService;
    }

    /**
     * View marks for pupil.
     * @param id pupil id
     * @return ModelAndView
     */
    @RequestMapping(value = "/pupil/{id}/marks")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewMarksByPupil(@PathVariable int id) {
        LOGGER.info("Getting list of marks for " + id + " pupil.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Pupil pupil = pupilDAO.getPupil(id, dbName);
        Map<String, Object> model = new HashMap<>();
        if (pupil == null) {
            String msg = "Pupil with id - " + id + " not found.";
            LOGGER.error(msg);
            model.put("message", msg);
            return new  ModelAndView("errorPage", model, HttpStatus.NOT_FOUND);
        }
        if (user.hasRole("PUPIL") && user.getId() != pupil.getId()) {
            return new  ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        crumbsMap.put("Класи", "/classes?page=1");
        crumbsMap.put("Учні", "/class/" + pupil.getPupilClass().getId() + "/pupils");
        crumbsMap.put("Оцінки", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("list", dao.getMarksByPupil(id, dbName));
        model.put("header", pupil.getName());
        model.put("subjectList", subjectDAO.getSubjectsByPupilClass(pupil.getPupilClass().getId(), dbName));
        LOGGER.info("Printing marks.");
        return new ModelAndView("markListForPupil", model);
    }

    /**
     * View marks for lesson.
     * @param id lesson id
     * @return ModelAndView
     */
    @RequestMapping(value = "/lesson/{id}/marks")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewMarksByLesson(@PathVariable int id) {
        LOGGER.info("Getting list of marks for " + id + " lesson.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        Lesson lesson = lessonDAO.getLesson(id, dbName);
        Map<String, Object> model = new HashMap<>();
        if (lesson == null) {
            String msg = "Lesson with id - " + id + " not found.";
            LOGGER.error(msg);
            model.put("message", msg);
            return new  ModelAndView("errorPage", model, HttpStatus.NOT_FOUND);
        }
        if (user.hasRole("PUPIL") &&
                pupilDAO.getPupil(user.getId(), dbName).getPupilClass().getId() !=
                        lesson.getTheme().getSubjectDetails().getPupilClass().getId()){
            return new  ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        if (user.hasRole("ADMIN")) {
            crumbsMap.put("Деталі предметів", "/subject-details?page=1");
        } else if (user.hasRole("TEACHER")) {
            crumbsMap.put("Деталі предметів", "/teacher/" + user.getId() + "/subject-details");
        } else if (user.hasRole("PUPIL")) {
            crumbsMap.put("Деталі предметів", "/pupil/" + user.getId() + "/subject-details");
        }
        crumbsMap.put("Теми", "/subject-details/" + lesson.getTheme().getSubjectDetails().getId() + "/themes");
        crumbsMap.put("Уроки", "/theme/" + lesson.getTheme().getId() + "/lessons");
        crumbsMap.put("Оцінки", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("list", new MarkList(dao.getMarksByLesson(id, dbName)));
        model.put("header", "Оцінки за урок");
        model.put("lesson", lesson);
        LOGGER.info("Printing marks.");
        return new ModelAndView("markListForLesson", model);
    }

    /**
     * Saving added mark.
     * @param list added marks
     * @return ModelAndView
     */
    @RequestMapping(value = "lesson/{id}/save-marks", method = RequestMethod.POST)
    @Secured("TEACHER")
    public ModelAndView saveAddedMarks(@ModelAttribute MarkList list, @PathVariable int id) throws Exception {
        LOGGER.info("Saving added marks.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        markService.saveMarks(list, user.getDbName());
        LOGGER.info("Redirect to list of marks for " + id + " lesson.");
        return new ModelAndView("redirect:/lesson/" + id + "/marks");
    }

    /**
     * View marks for subject details.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-details/{id}/marks")
    @Secured({"ADMIN", "TEACHER", "PUPIL"})
    public ModelAndView viewMarksBySubjectDetails(@PathVariable int id, @RequestParam("page") int page) {
        LOGGER.info("Getting list of marks for " + id + " subject details.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dbName = user.getDbName();
        SubjectDetails subjectDetails = subjectDetailsDAO.getSubjectDetails(id, dbName);
        Map<String, Object> model = new HashMap<>();
        if (subjectDetails == null) {
            String msg = "Subject details with id - " + id + " not found.";
            LOGGER.error(msg);
            model.put("message", msg);
            return new  ModelAndView("errorPage", model, HttpStatus.NOT_FOUND);
        }
        if (user.hasRole("PUPIL") &&
                pupilDAO.getPupil(user.getId(), dbName).getPupilClass().getId() !=
                        subjectDetails.getPupilClass().getId()){
            return new  ModelAndView("errorPage", HttpStatus.FORBIDDEN);
        }
        School school = schoolDAO.getSchool(Integer.parseInt(dbName));
        model.put("school", school);
        model.put("header", "Список оцінок");
        Map<String, Map<Integer, List<Mark>>> marks = markService.getMarksForSubject(subjectDetails, page, marksPerPage, dbName);
        Map<Integer, List<Lesson>> lessons = markService.getLessonsForSubject(subjectDetails, page, marksPerPage, dbName);
        Map<String, Mark> semesterMarks = markService.getSemesterMarks(subjectDetails, dbName);
        int count = markService.getCountOfMarks(subjectDetails.getId(), dbName);
        PaginationController paginationController = new PaginationController(count, marksPerPage, page);
        Map<String, String> crumbsMap = new LinkedHashMap<>();
        if (user.hasRole("ADMIN")) {
            crumbsMap.put("Деталі предметів", "/subject-details?page=1");
        } else if (user.hasRole("TEACHER")) {
            crumbsMap.put("Деталі предметів", "/teacher/" + user.getId() + "/subject-details");
        } else if (user.hasRole("PUPIL")) {
            crumbsMap.put("Деталі предметів", "/pupil/" + user.getId() + "/subject-details");
        }
        crumbsMap.put("Оцінки", "");
        model.put("crumbs", BreadcrumbsController.getBreadcrumbs(crumbsMap));
        model.put("pagination", paginationController.makePagingLinks("marks"));
        model.put("page", page);
        model.put("marks", marks);
        model.put("lessons", lessons);
        model.put("semesterMarks", semesterMarks);
        model.put("subjectDetails", subjectDetails);
        model.put("list", new MarkList(new ArrayList<>()));
        LOGGER.info("Printing marks.");
        return new ModelAndView("marks", model);
    }

    /**
     * View marks for subject details.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-details/{id}/save-marks")
    @Secured("TEACHER")
    public ModelAndView saveMarksBySubjectDetails(@PathVariable int id,
                                                  @ModelAttribute MarkList list,
                                                  @RequestParam("page") int page) throws Exception {
        LOGGER.info("Saving added marks.");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        markService.saveMarks(list, user.getDbName());
        return new ModelAndView("redirect:/subject-details/" + id + "/marks?page=" + page);
    }
}

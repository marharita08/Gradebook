package org.example.controllers;

import org.apache.log4j.Logger;
import org.example.dao.interfaces.*;
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
    private final SemesterDAO semesterDAO;
    private final PupilDAO pupilDAO;
    private static final int subjectDetailsPerPage = 25;
    private static final Logger LOGGER = Logger.getLogger(SubjectDetailsController.class.getName());

    public SubjectDetailsController(SubjectDetailsDAO dao,
                                    PupilClassDAO classDAO,
                                    TeacherDAO teacherDAO,
                                    SubjectDAO subjectDAO, SemesterDAO semesterDAO, PupilDAO pupilDAO) {
        this.dao = dao;
        this.classDAO = classDAO;
        this.teacherDAO = teacherDAO;
        this.subjectDAO = subjectDAO;
        this.semesterDAO = semesterDAO;
        this.pupilDAO = pupilDAO;
    }

    /**
     * Getting page to view all subject details list.
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-details")
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
        model.put("pagination", paginationController.makePagingLinks("viewAllSubjectDetails"));
        model.put("header", "Subject Details List");
        model.put("pageNum", page);
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page for subject details adding.
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-detail")
    public ModelAndView addSubjectDetails() throws Exception {
        List<PupilClass> pupilClasses = classDAO.getAllPupilClasses();
        if (pupilClasses == null) {
            throw new Exception("There are no classes to add subject details.");
        }
        List<Subject> subjects = subjectDAO.getAllSubjects();
        if (subjects == null) {
            throw new Exception("There are no subjects to add subject details.");
        }
        List<Semester> semesters = semesterDAO.getAllSemesters();
        if (semesters == null) {
            throw new Exception("There are no semesters to add subject details.");
        }
        LOGGER.info("Add new subject details.");
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        model.put("command", new SubjectDetails());
        model.put("selectedClass", 0);
        model.put("selectedTeacher", 0);
        model.put("selectedSubject", 0);
        model.put("selectedSemester", 0);
        model.put("classList", pupilClasses);
        model.put("teacherList", teacherDAO.getAllTeachers());
        model.put("subjectList", subjectDAO.getAllSubjects());
        model.put("semesterList", semesterDAO.getAllSemesters());
        model.put("title", "Add Subject Details");
        model.put("formAction", "subject-detail");
        LOGGER.info("Printing form for input subject details data.");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving added subject details.
     * @param subjectDetails added subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-detail", method = RequestMethod.POST)
    public ModelAndView saveAddedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) throws Exception {
        LOGGER.info("Saving added subject details.");
        dao.addSubjectDetails(subjectDetails);
        LOGGER.info("Redirect to subject details list.");
        return new ModelAndView("redirect:/subject-details?page=1");
    }

    /**
     * Getting page for subject details editing.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-detail/{id}", method = RequestMethod.GET)
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
        model.put("selectedSemester", subjectDetails.getSemester().getId());
        model.put("classList", classDAO.getAllPupilClasses());
        model.put("teacherList", teacherDAO.getAllTeachers());
        model.put("subjectList", subjectDAO.getAllSubjects());
        model.put("semesterList", semesterDAO.getAllSemesters());
        model.put("title", "Edit subject details");
        model.put("formAction", "../subject-details");
        model.put("toRoot", "../");
        LOGGER.info("Printing form for changing subject details data.");
        return new ModelAndView("subjectDetailsForm", model);
    }

    /**
     * Saving edited subject details.
     * @param subjectDetails edited subject details
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-detail/{id}", method = RequestMethod.POST)
    public ModelAndView saveEditedSubjectDetails(@ModelAttribute SubjectDetails subjectDetails) throws Exception {
        LOGGER.info("Saving edited subject details.");
        dao.updateSubjectDetails(subjectDetails);
        LOGGER.info("Redirect to subject details list.");
        return new ModelAndView("redirect:/subject-details?page=1");
    }

    /**
     * Delete subject details by id.
     * @param id subject details id
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject-detail/{id}/delete")
    public ModelAndView deleteSubjectDetails(@PathVariable int id, @RequestParam("page") int pageNum) {
        LOGGER.info("Deleting subject details " + id + ".");
        SubjectDetails subjectDetails = dao.getSubjectDetails(id);
        if (subjectDetails == null) {
            LOGGER.error("Subject details" + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        dao.deleteSubjectDetails(id);
        LOGGER.info("Redirect to subject details list on page " + pageNum + ".");
        return new ModelAndView("redirect:/subject-details?page=" + pageNum);
    }

    /**
     * Getting page to view subject details by teacher.
     * @return ModelAndView
     */
    @RequestMapping(value = "/teacher/{id}/subject-detail")
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.hasRole("PUPIL") && !user.hasRole("ADMIN")) {
            Pupil pupil = pupilDAO.getPupil(user.getId());
            model.put("pupilClass", pupil.getPupilClass());
        }
        model.put("list", list);
        model.put("header", "Subjects of " + teacher.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by class.
     * @return ModelAndView
     */
    @RequestMapping(value = "/class/{id}/subject-detail")
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.hasRole("PUPIL") && !user.hasRole("ADMIN")) {
            Pupil pupil = pupilDAO.getPupil(user.getId());
            model.put("pupilClass", pupil.getPupilClass());
        }
        model.put("list", list);
        model.put("header", "Subjects of " + pupilClass.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by subject.
     * @return ModelAndView
     */
    @RequestMapping(value = "/subject/{id}/subject-detail")
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.hasRole("PUPIL") && !user.hasRole("ADMIN")) {
            Pupil pupil = pupilDAO.getPupil(user.getId());
            model.put("pupilClass", pupil.getPupilClass());
        }
        model.put("list", list);
        model.put("header", "Subject " + subject.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by semester and teacher.
     * @return ModelAndView
     */
    @RequestMapping(value = "/semester/{semesterID}/teacher/{teacherID}")
    public ModelAndView viewSubjectDetailsBySemesterAndTeacher(@PathVariable int semesterID, @PathVariable int teacherID) {
        LOGGER.info("Getting list of subject details by " + semesterID + " semester and " + teacherID + " teacher.");
        Teacher teacher = teacherDAO.getTeacher(teacherID);
        if (teacher == null) {
            LOGGER.error("Teacher " + teacherID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Semester semester = semesterDAO.getSemester(semesterID);
        if (semester == null) {
            LOGGER.error("Semester " + semesterID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsBySemesterAndTeacher(semesterID, teacherID);
        model.put("list", list);
        model.put("header", "Subjects of " + teacher.getName() + " for " + semester.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../../../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by semester and pupil.
     * @return ModelAndView
     */
    @RequestMapping(value = "/semester/{semesterID}/pupil/{pupilID}")
    public ModelAndView viewSubjectDetailsBySemesterAndPupil(@PathVariable int semesterID, @PathVariable int pupilID) {
        LOGGER.info("Getting list of subject details by " + semesterID + " semester and " + pupilID + " teacher.");
        Pupil pupil = pupilDAO.getPupil(pupilID);
        if (pupil == null) {
            LOGGER.error("Pupil " + pupilID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        Semester semester = semesterDAO.getSemester(semesterID);
        if (semester == null) {
            LOGGER.error("Semester " + semesterID + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Form a model.");
        Map<String, Object> model = new HashMap<>();
        List<SubjectDetails> list = dao.getSubjectDetailsBySemesterAndPupil(semesterID, pupilID);
        model.put("list", list);
        model.put("header", "Subjects of " + pupil.getPupilClass().getName() + " for " + semester.getName());
        model.put("pagination", "");
        model.put("pageNum", 1);
        model.put("toRoot", "../../../");
        LOGGER.info("Printing subject details list.");
        return new ModelAndView("viewSubjectDetailsList", model);
    }

    /**
     * Getting page to view subject details by pupil.
     * @return ModelAndView
     */
    @RequestMapping(value = "/pupil/{id}/subject-details")
    public ModelAndView viewSubjectDetailsByPupil(@PathVariable int id) {
        LOGGER.info("Getting list of subject details by " + id + " pupil.");
        Pupil pupil = pupilDAO.getPupil(id);
        if (pupil == null) {
            LOGGER.error("Pupil " + id + " not found.");
            return new  ModelAndView("errorPage", HttpStatus.NOT_FOUND);
        }
        return new ModelAndView("redirect:../class/" + pupil.getPupilClass().getId() + "/subject-details");
    }

    @RequestMapping(value = "/subject-details/search")
    @ResponseBody
    public List<SubjectDetails> searchSubjectDetails(@RequestParam("val") String val,
                                            @RequestParam("param")String param) throws Exception {
        LOGGER.info("Searching subject details by " + param + ".");
        List<SubjectDetails> list;
        if(!val.isEmpty()) {
            list = dao.searchSubjectDetails(val, param);
        } else {
            list = dao.getSubjectDetailsByPage(1, subjectDetailsPerPage);
        }
        return list;
    }
}

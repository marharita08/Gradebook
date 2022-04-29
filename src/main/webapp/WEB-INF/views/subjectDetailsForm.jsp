<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="org.example.entities.Teacher" %>
<%@ page import="org.example.entities.Subject" %>
<%@ page import="org.example.entities.Semester" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title><%=request.getAttribute("title")%></title>
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="../css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <div class="card" style="width: 50%">
                    <br/>
                    <h2 align="center"><%=request.getAttribute("title")%></h2>
                    <form:form>
                        <br/>
                        <div class="row">
                            <div class="col-25">
                                <label>Class</label>
                            </div>
                            <div class="col-75">
                                <form:select path="pupilClass.id">
                                    <%
                                        for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("classList")) {
                                    %>
                                    <option value="<%=pupilClass.getId()%>"
                                            <%=(int)request.getAttribute("selectedClass") == pupilClass.getId() ? "selected='selected'":""%>>
                                        <%=pupilClass.getName()%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </form:select><br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                <label>Teacher</label>
                            </div>
                            <div class="col-75">
                                <form:select path="teacher.id">
                                    <option value="0">-</option>
                                    <%
                                        for (Teacher teacher:(List<Teacher>)request.getAttribute("teacherList")) {
                                    %>
                                    <option value="<%=teacher.getId()%>"
                                            <%=(int)request.getAttribute("selectedTeacher") == teacher.getId() ? "selected='selected'":""%>>
                                        <%=teacher.getName()%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </form:select><br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                <label>Subject</label>
                            </div>
                            <div class="col-75">
                                <form:select path="subject.id">
                                    <%
                                        for (Subject subject:(List<Subject>)request.getAttribute("subjectList")) {
                                    %>
                                    <option value="<%=subject.getId()%>"
                                            <%=(int)request.getAttribute("selectedSubject") == subject.getId() ? "selected='selected'":""%>>
                                        <%=subject.getName()%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </form:select><br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                <label>Semester</label>
                            </div>
                            <div class="col-75">
                                <form:select path="semester.id">
                                    <%
                                        for (Semester semester:(List<Semester>)request.getAttribute("semesterList")) {
                                    %>
                                    <option value="<%=semester.getId()%>"
                                            <%=(int)request.getAttribute("selectedSemester") == semester.getId() ? "selected='selected'":""%>>
                                        <%=semester.getSchoolYear().getName() + " " + semester.getName()%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </form:select><br/><br/>
                            </div>
                        </div>
                        <form:input path="id" type="hidden"/>
                        <button onclick="history.back()" type="button" class="bg-primary">
                            <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                            <div class="inline">Cancel</div>
                        </button>
                        <button formmethod="post" formaction="<%=request.getAttribute("formAction")%>" class="bg-primary">
                            <div class="inline"><i class='material-icons'>save</i></div>
                            <div class="inline">Save</div>
                        </button>
                        <br/><br/>
                    </form:form>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>

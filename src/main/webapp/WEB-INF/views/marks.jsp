<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Mark list</title>
        <link rel="icon" type="img/png" href="../images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="../css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <ul class="breadcrumb"><%=request.getAttribute("crumbs")%></ul>
                <h2><%=request.getAttribute("header")%></h2>
                <%
                    SubjectDetails subjectDetails = (SubjectDetails) request.getAttribute("subjectDetails");
                    Teacher teacher = subjectDetails.getTeacher();
                %>
                <table>
                    <tr>
                        <td>Class:</td>
                        <td><%=subjectDetails.getPupilClass().getName()%></td>
                    </tr>
                    <tr>
                        <td>Subject:</td>
                        <td><%=subjectDetails.getSubject().getName()%></td>
                    </tr>
                    <%
                        if(teacher != null) {
                    %>
                    <tr>
                        <td>Teacher:</td>
                        <td><%=teacher.getName()%></td>
                    </tr>
                    <%
                        }
                    %>
                </table>
                <ul class="pagination"><%=request.getAttribute("pagination")%></ul>
                <form action="save-marks?page=<%=request.getAttribute("page")%>" method="post">
                    <table class="borders">
                        <%
                            Map<Integer, List<Lesson>> lessons = (Map<Integer, List<Lesson>>)request.getAttribute("lessons");
                            Map<String, Mark> semesterMarks = (Map<String, Mark>) request.getAttribute("semesterMarks");
                            Map<String, Map<Integer, List<Mark>>> marks = (Map<String, Map<Integer, List<Mark>>>) request.getAttribute("marks");
                            if (lessons.isEmpty()) {
                        %>
                                <tr class="borders">
                                    <td>List of marks is empty</td>
                                </tr>
                        <%
                            } else {
                        %>
                                <tr class="borders">
                                    <td class="borders"></td>
                                    <%
                                        for (Map.Entry<Integer, List<Lesson>> entry:lessons.entrySet()) {
                                            for (Lesson lesson: entry.getValue()) {
                                                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                                String strDate = dateFormat.format(lesson.getDate());
                                    %>
                                                <td class="borders"><p class="dates"><%=strDate%></p></td>
                                        <%
                                            }
                                        %>
                                            <td class="borders"><p class="dates">Thematic</p></td>
                                    <%
                                        }
                                    %>
                                    <td class="borders"><p class="dates">Semester</p></td>
                                </tr>
                                <%
                                    int i = 0;
                                    for (Map.Entry<String, Map<Integer, List<Mark>>> entry:marks.entrySet()) {
                                %>
                                        <tr class="borders">
                                            <td class="borders"><%=entry.getKey()%></td>
                                            <%
                                                for (Map.Entry<Integer, List<Mark>> entry1:entry.getValue().entrySet()) {
                                                    for (Mark mark:entry1.getValue()) {
                                            %>
                                                        <td class="borders">
                                                            <%
                                                                if(teacher != null && teacher.getId() == currUser.getId()
                                                                    && mark.getLesson() != null) {
                                                            %>
                                                                    <input name="list[<%=i%>].id"
                                                                           value="<%=mark.getId()%>"
                                                                           type="hidden"/>
                                                                    <input name="list[<%=i%>].pupil.id"
                                                                           value="<%=mark.getPupil().getId()%>"
                                                                           type="hidden"/>
                                                                    <input name="list[<%=i%>].lesson.id"
                                                                           value="<%=mark.getLesson().getId()%>"
                                                                           type="hidden"/>
                                                                    <input name="list[<%=i++%>].mark"
                                                                           value="<%=mark.getMark()==null?"":mark.getMark()%>"
                                                                           pattern="a|[1-9]|1[0-2]"
                                                                           oninvalid="this.setCustomValidity('Inputted value is invalid')"
                                                                           oninput="this.setCustomValidity('')"
                                                                           class="width-40"/>
                                                            <%
                                                                } else if (currUser.hasRole("ADMIN") || currUser.hasRole("TEACHER")
                                                                    || currUser.getId() == mark.getPupil().getId()) {
                                                            %>
                                                                    <%=mark.getMark()==null?"":mark.getMark()%>
                                                            <%
                                                                }
                                                            %>
                                                        </td>
                                            <%
                                                    }
                                                }
                                                Mark mark = semesterMarks.get(entry.getKey());
                                            %>
                                            <td class="borders">
                                                <%
                                                    if (currUser.hasRole("ADMIN") || currUser.hasRole("TEACHER")
                                                        || currUser.getId() == mark.getPupil().getId()) {
                                                %>
                                                        <%=mark.getMark() == null ? "" : mark.getMark()%>
                                                <%
                                                    }
                                                %>
                                            </td>
                                        </tr>
                        <%
                                    }
                            }
                        %>
                    </table>
                    <%
                        if(teacher!=null && teacher.getId() == currUser.getId()) {
                    %>
                            <button type="submit" class="bg-primary">
                                <div class="inline"><i class='material-icons'>save</i></div>
                                <div class="inline">Save</div>
                            </button>
                    <%
                        }
                    %>
                </form>
                <br/>
                <button onclick='location.href="<%=root%>index.jsp"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Menu</div>
                </button>
                <button onclick=history.back() class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Back</div>
                </button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>

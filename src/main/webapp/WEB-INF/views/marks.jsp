<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.*" %>
<%@ page import="java.util.Map" %>
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
                <h2><%=request.getAttribute("header")%></h2>
                <%
                    SubjectDetails subjectDetails = (SubjectDetails) request.getAttribute("subjectDetails");
                %>
                <p>Subject:<%=subjectDetails.getSubject().getName()%></p>
                <p>Teacher:<%=subjectDetails.getTeacher().getName()%></p>
                <p>Class:<%=subjectDetails.getPupilClass().getName()%></p>
                <table>
                    <tr>
                        <td></td>
                        <%
                            Map<Integer, List<Lesson>> lessons = (Map<Integer, List<Lesson>>)request.getAttribute("lessons");
                            for (Map.Entry<Integer, List<Lesson>> entry:lessons.entrySet()) {
                                for (Lesson lesson: entry.getValue()) {
                        %>
                                    <td><p class="dates"><%=lesson.getDate()%></p></td>
                        <%
                                }
                        %>
                            <td><p class="dates">Thematic</p></td>
                        <%
                            }
                        %>
                        <td><p class="dates">Semester</p></td>
                    </tr>
                    <%
                        Map<String, Mark> semesterMarks = (Map<String, Mark>) request.getAttribute("semesterMarks");
                        Map<String, Map<Integer, List<Mark>>> marks = (Map<String, Map<Integer, List<Mark>>>) request.getAttribute("marks");
                        for (Map.Entry<String, Map<Integer, List<Mark>>> entry:marks.entrySet()) {
                    %>
                    <tr>
                        <td><%=entry.getKey()%></td>
                        <%
                            for (Map.Entry<Integer, List<Mark>> entry1:entry.getValue().entrySet()) {
                                for (Mark mark:entry1.getValue()) {
                        %>
                                    <td>
                                        <%
                                            if (currUser.hasRole("ADMIN") || currUser.hasRole("TEACHER")
                                                    || currUser.getId() == mark.getPupil().getId()) {
                                        %>
                                                <%=mark.getMark()!=0?mark.getMark():""%>
                                        <%
                                            }
                                        %>
                                    </td>
                        <%
                                }
                            }
                            Mark mark = semesterMarks.get(entry.getKey());
                        %>
                        <td>
                            <%
                                if (currUser.hasRole("ADMIN") || currUser.hasRole("TEACHER")
                                        || currUser.getId() == mark.getPupil().getId()) {
                            %>
                                    <%=mark != null ? mark.getMark() : ""%>
                            <%
                                }
                            %>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </table>
                <br/>
                <button onclick='location.href="../index.jsp"'>Menu</button>
                <button onclick=history.back()>Back</button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>

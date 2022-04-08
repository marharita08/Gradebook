<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.*" %>
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
                    Lesson lesson = (Lesson) request.getAttribute("lesson");
                    Theme theme = lesson.getTheme();
                    SubjectDetails subjectDetails = theme.getSubjectDetails();
                    Teacher teacher = subjectDetails.getTeacher();
                %>
                <p>Subject:<%=subjectDetails.getSubject().getName()%></p>
                <%
                    if (teacher != null) {
                %>
                        <p>Teacher:<%=teacher.getName()%></p>
                <%
                    }
                %>
                <p>Theme:<%=theme.getName()%></p>
                <p>Date:<%=lesson.getDate()%></p>
                <p>Topic:<%=lesson.getTopic()%></p>
                <table id="myTable">
                    <tr>
                        <th>Pupil</th>
                        <th>Mark</th>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <%
                                if(teacher != null && currUser.getId() == teacher.getId()) {
                            %>
                                    <th>EDIT</th>
                                    <th>DELETE</th>
                            <%
                                }
                            %>
                        </sec:authorize>
                    </tr>
                    <tr>
                        <th><input type="text" id="pupil" onkeyup="filter(id, 0)" class="search"></th>
                        <th></th>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <%
                                if(teacher != null && currUser.getId() == teacher.getId()) {
                            %>
                                    <th></th>
                                    <th></th>
                            <%
                                }
                            %>
                        </sec:authorize>
                    </tr>
                    <%
                        for (Mark mark:(List<Mark>)request.getAttribute("list")) {
                    %>
                            <tr>
                                <td><%=mark.getPupil().getName()%></td>
                                <%
                                    if(mark.getMark() != 0 && (currUser.hasRole("ADMIN")
                                        || currUser.hasRole("TEACHER") || currUser.getId() == mark.getPupil().getId())) {
                                %>
                                        <td><%=mark.getMark()%></td>
                                        <sec:authorize access="hasAuthority('TEACHER')">
                                            <%
                                                if(teacher != null && currUser.getId() == teacher.getId()) {
                                            %>
                                                    <td><a href="../mark/<%=mark.getId()%>">edit mark</a></td>
                                                    <td><a href="../mark/<%=mark.getId()%>/delete">delete mark</a></td>
                                            <%
                                                }
                                            %>
                                        </sec:authorize>
                                <%
                                    } else {
                                %>
                                        <td></td>
                                        <sec:authorize access="hasAuthority('TEACHER')">
                                            <%
                                                if(teacher != null && currUser.getId() == teacher.getId()) {
                                            %>
                                                    <td></td>
                                                    <td></td>
                                            <%
                                                }
                                            %>
                                        </sec:authorize>
                                <%
                                    }
                                %>
                            </tr>
                    <%
                        }
                    %>
                </table>
                <br/>
                <button onclick='location.href="../index.jsp"'>Menu</button>
                <button onclick=history.back()>Back</button>
                <button onclick='location.href="../theme/<%=theme.getId()%>/lessons"'>To lessons</button>
                <sec:authorize access="hasAuthority('TEACHER')">
                    <%
                        if(teacher != null && currUser.getId() == teacher.getId()) {
                    %>
                            <button onclick='location.href="../mark/<%=lesson.getId()%>"'>Add mark</button>
                    <%
                        }
                    %>
                </sec:authorize>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/filter.js"%>
    </script>
</html>

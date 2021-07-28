<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Mark" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ page import="org.example.entities.Lesson" %>
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
<p>Subject:<%=request.getAttribute("subject")%></p>
<p>Teacher:<%=request.getAttribute("teacher")%></p>
<p>Class:<%=request.getAttribute("class")%></p>

<table>
    <tr>
        <td></td>
        <% for (Lesson lesson:(List<Lesson>)request.getAttribute("lessonList")) {%>
        <td><p class="dates"><%=lesson.getDate()%></p></td>
        <%}%>
    </tr>
    <% for (Pupil pupil:(List<Pupil>)request.getAttribute("pupilList")) { %>

    <tr>
        <td><%=pupil.getName()%></td>

        <% for (Lesson lesson:(List<Lesson>)request.getAttribute("lessonList")) {%>
            <td>
            <%for (Mark mark:(List<Mark>)request.getAttribute("markList")) {%>
            <%=mark.getLesson().equals(lesson)&&mark.getPupil().equals(pupil)?mark.getMark():""%>
        <%}%>
            </td>
        <%}%>

    </tr>
    <% } %>
</table>
<br/>
<button onclick='location.href="../index.jsp"'>Menu</button>
<button onclick=history.back()>Back</button>
</div>
</div>
<%@include file="footer.jsp"%>
</body>
</html>

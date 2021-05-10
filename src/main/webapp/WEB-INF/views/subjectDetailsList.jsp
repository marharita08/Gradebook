<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.SubjectDetails" %><%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 26.04.2021
  Time: 17:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Subject details list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%if(request.getAttribute("header") != null) {%>
<h2><%=request.getAttribute("header")%></h2>
<%}%>
<table>
    <% for (SubjectDetails subjectDetails:(List<SubjectDetails>)request.getAttribute("list")) { %>
    <tr>
        <%if(request.getAttribute("param") != "class") {%>
            <td><%=subjectDetails.getPupilClass().getName()%></td>
        <%}%>
        <%if(request.getAttribute("param") != "subject") {%>
            <td><%=subjectDetails.getSubject().getName()%></td>
        <%}%>
        <%if(request.getAttribute("param") != "teacher") {%>
            <td><%=subjectDetails.getTeacher().getName()%></td>
        <%}%>
        <td><a href="/Gradebook/viewLessonsByPupilClassAndSubject/<%=subjectDetails.getPupilClass().getId()%>/<%=subjectDetails.getSubject().getId()%>">view lessons</a></td>
        <td><a href="/Gradebook/addLesson/<%=subjectDetails.getId()%>">add lesson</a></td>
    </tr>
    <% } %>
</table>
<br/>
<button onclick='location.href="/Gradebook/index.jsp"'>Menu</button>
<button onclick=history.back()>Back</button>
</body>
</html>


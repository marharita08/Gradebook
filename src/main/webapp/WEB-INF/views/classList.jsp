<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 26.04.2021
  Time: 17:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Class list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
    <h2><%=request.getAttribute("header")%></h2>
    <table>
    <% for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("list")) { %>
        <tr>
            <td><%=pupilClass.getName()%></td>
            <td><a href="<%= "/Gradebook/viewPupilsByPupilClass/" + pupilClass.getId()%>">view pupil list</a></td>
            <td><a href="<%= "/Gradebook/viewSubjectsByPupilClass/" + pupilClass.getId()%>">view subjects</a></td>
            <td><a href="<%= "/Gradebook/viewTeachersByPupilClass/" + pupilClass.getId()%>">view teacher list</a></td>
            <td><a href="<%= "/Gradebook/viewSubjectDetailsByPupilClass/" + pupilClass.getId()%>">view teacher-subject list</a></td>
        </tr>
    <% } %>
    </table>
    <br/>
    <button onclick='location.href="/Gradebook/index.jsp"'>Menu</button>
    <button onclick='history.back()'>Back</button>
</body>
</html>

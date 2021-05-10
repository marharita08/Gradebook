<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Teacher" %><%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 26.04.2021
  Time: 17:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Teacher list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<h2><%=request.getAttribute("header")%></h2>
<table>
    <% for (Teacher teacher:(List<Teacher>)request.getAttribute("list")) { %>
    <tr>
        <td><%=teacher.getName()%></td>
        <td><a href="/Gradebook/viewSubjectsByTeacher/<%=teacher.getId()%>">view subjects</a></td>
        <td><a href="/Gradebook/viewSubjectDetailsByTeacher/<%=teacher.getId()%>">view subject-class list</a></td>
    </tr>
    <% } %>
</table>
<br/>
<button onclick='location.href="/Gradebook/index.jsp"'>Menu</button>
<button onclick='history.back()'>Back</button>
</body>
</html>

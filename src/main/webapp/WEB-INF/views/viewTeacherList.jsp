<%@ page import="org.example.entities.Teacher" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 12.04.2021
  Time: 16:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>View Teacher List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<h2 align="center">Teacher List: </h2>
<div align="center">
    <select OnChange="sortTable(value)">
        <option>Sort by</option>
        <option value="0">ID</option>
        <option value="1">Name</option>
        <option value="2">Position</option>
        <option value="3">Chief</option>
    </select><br/><br/>
<table id="myTable">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Position</th>
        <th>Chief</th>
        <th>EDIT</th>
        <th>DELETE</th>
    </tr>
    <tr>
        <th><input type="text" id="id" onkeyup="filter(id, 0)" class="filters"></th>
        <th><input type="text" id="name" onkeyup="filter(id, 1)" class="filters"></th>
        <th><input type="text" id="position" onkeyup="filter(id, 2)" class="filters"></th>
        <th><input type="text" id="chief" onkeyup="filter(id, 3)" class="filters"></th>
        <th></th>
        <th></th>
    </tr>

    <% for (Teacher teacher:(List<Teacher>)request.getAttribute("list")) { %>
        <tr>
            <td><%=teacher.getId()%></td>
            <td><%=teacher.getName()%></td>
            <td><%=teacher.getPosition()%></td>
            <% if(teacher.getChief() != null) { %>
                <td><%=teacher.getChief().getId() + " " + teacher.getChief().getName()%></td>
            <% } else { %>
                <td>-</td>
            <%}%>
            <td><a href="editTeacher/<%=teacher.getId()%>">Edit</a></td>
            <td><a href="deleteTeacher/<%=teacher.getId()%>">Delete</a></td>
        </tr>
    <% } %>
</table>
    <br/>
    <button onclick='location.href="index.jsp"'>Menu</button>
    <button onclick='location.href="addTeacher"'>Add</button>
</div>


</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.SubjectDetails" %>
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
    <title>View Subject Details List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<h2 align="center">Subject Details List: </h2>
<div align="center">
    <select OnChange="sortTable(value)">
        <option>Sort by</option>
        <option value="0">ID</option>
        <option value="1">Class</option>
        <option value="2">Teacher</option>
        <option value="3">Subject</option>
    </select><br/><br/>
    <table id="myTable">
        <tr>
            <th>ID</th>
            <th>Class</th>
            <th>Teacher</th>
            <th>Subject</th>
            <th>EDIT</th>
            <th>DELETE</th>
        </tr>
        <tr>
            <th><input type="text" id="id" onkeyup="filter(id, 0)" class="filters"></th>
            <th><input type="text" id="class" onkeyup="filter(id, 1)" class="filters"></th>
            <th><input type="text" id="teacher" onkeyup="filter(id, 2)" class="filters"></th>
            <th><input type="text" id="subject" onkeyup="filter(id, 3)" class="filters"></th>
            <th></th>
            <th></th>
        </tr>

        <% for (SubjectDetails subjectDetails:(List<SubjectDetails>)request.getAttribute("list")) { %>
        <tr>
            <td><%=subjectDetails.getId()%></td>
            <td><%=subjectDetails.getPupilClass().getName()%></td>
            <% if(subjectDetails.getTeacher() != null) { %>
            <td><%=subjectDetails.getTeacher().getName()%></td>
            <% } else { %>
            <td>-</td>
            <%}%>
            <td><%=subjectDetails.getSubject().getName()%></td>
            <td><a href="editSubjectDetails/<%=subjectDetails.getId()%>">Edit</a></td>
            <td><a href="deleteSubjectDetails/<%=subjectDetails.getId()%>">Delete</a></td>
        </tr>
        <% } %>
    </table>
    <br/>
    <button onclick='location.href="index.jsp"'>Menu</button>
    <button onclick='location.href="addSubjectDetails"'>Add</button>
</div>


</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

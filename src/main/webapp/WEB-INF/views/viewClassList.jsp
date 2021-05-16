<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="org.example.controllers.PaginationController" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>View Class List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<h2 align="center">Class List: </h2>
<div align="center">
    <select OnChange="sortTable(value)">
        <option>Sort by</option>
        <option value="0">ID</option>
        <option value="1">Grade</option>
        <option value="2">Name</option>
    </select><br/><br/>
    <ul class="pagination"><%=request.getAttribute("pagination")%></ul>
    <table id="myTable">
        <tr>
            <th>ID</th>
            <th>Grade</th>
            <th>Name</th>
            <th>EDIT</th>
            <th>DELETE</th>
        </tr>
        <tr>
            <th><input type="text" id="id" onkeyup="filter(id, 0)" class="filters"></th>
            <th><input type="text" id="grade" onkeyup="filter(id, 1)" class="filters"></th>
            <th><input type="text" id="name" onkeyup="filter(id, 2)" class="filters"></th>
            <th></th>
            <th></th>
        </tr>

        <% for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("list")) { %>
        <tr>
            <td><%=pupilClass.getId()%></td>
            <td><%=pupilClass.getGrade()%></td>
            <td><%=pupilClass.getName()%></td>
            <td><a href="editClass/<%=pupilClass.getId()%>">Edit</a></td>
            <td><a href="deleteClass/<%=pupilClass.getId()%>">Delete</a></td>
        </tr>
        <% } %>
    </table>
    <br/>
    <button onclick='location.href="index.jsp"'>Menu</button>
    <button onclick='location.href="addClass"'>Add</button>
</div>


</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>
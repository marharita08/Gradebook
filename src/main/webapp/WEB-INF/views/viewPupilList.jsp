<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>View Pupil List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<%@include file="header.jsp"%>
<h2 align="center">Pupil List: </h2>
<div align="center">
    <select OnChange="sortTable(value)">
        <option>Sort by</option>
        <option value="0">ID</option>
        <option value="1">Class</option>
        <option value="2">Name</option>
    </select><br/>
    <ul class="pagination"><%=request.getAttribute("pagination")%></ul>
    <table id="myTable">
        <tr>
            <th>ID</th>
            <th>Class</th>
            <th>Name</th>
            <th>EDIT</th>
            <th>DELETE</th>
        </tr>
        <tr>
            <th><input type="text" id="id" onkeyup="filter(id, 0)" class="filters"></th>
            <th><input type="text" id="class" onkeyup="filter(id, 1)" class="filters"></th>
            <th><input type="text" id="name" onkeyup="filter(id, 2)" class="filters"></th>
            <th></th>
            <th></th>
        </tr>

        <% for (Pupil pupil:(List<Pupil>)request.getAttribute("list")) { %>
        <tr>
            <td><%=pupil.getId()%></td>
            <% if(pupil.getPupilClass() != null) { %>
                <td><%=pupil.getPupilClass().getName()%></td>
            <% } else { %>
                <td>-</td>
            <%}%>
            <td><%=pupil.getName()%></td>
            <td><a href="editPupil/<%=pupil.getId()%>">Edit</a></td>
            <td><a href="deletePupil/<%=pupil.getId()%>">Delete</a></td>
        </tr>
        <% } %>
    </table>
    <br/>
    <button onclick='location.href="/Gradebook/"'>Menu</button>
    <button onclick='location.href="addPupil"'>Add</button>
</div>
<%@include file="footer.jsp"%>

</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>
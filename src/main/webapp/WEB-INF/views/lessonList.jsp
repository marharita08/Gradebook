<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Lesson" %>
<%@ page import="org.example.controllers.PaginationController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lessons list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<h2><%=request.getAttribute("header")%></h2>
<%if(request.getAttribute("teacher") != null) {%>
<p><%=request.getAttribute("teacher")%></p>
<%}%>
<ul class="pagination"><%=request.getAttribute("pagination")%></ul>
<table id="myTable">
    <tr>
        <th>Date</th>
        <th>Topic</th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
    </tr>
    <tr>
        <th><input type="text" id="date" onkeyup="filter(id, 0)" class="filters"></th>
        <th><input type="text" id="topic" onkeyup="filter(id, 1)" class="filters"></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
    </tr>
    <% for (Lesson lesson:(List<Lesson>)request.getAttribute("list")) { %>
    <tr>
        <td><%=lesson.getDate()%></td>
        <td><%=lesson.getTopic()%></td>
        <td><a href="/Gradebook/viewMarksByLesson/<%=lesson.getId()%>">view marks</a></td>
        <td><a href="/Gradebook/addMark/<%=lesson.getId()%>">add mark</a></td>
        <td><a href="/Gradebook/editLesson/<%=lesson.getId()%>">edit lesson</a></td>
        <td><a href="/Gradebook/deleteLesson/<%=lesson.getId()%>">delete lesson</a></td>
    </tr>
    <% } %>
</table>
<br/>
<button onclick='location.href="/Gradebook/index.jsp"'>Menu</button>
<button onclick=history.back()>Back</button>
<button onclick='location.href="/Gradebook/addLesson/<%=request.getAttribute("subjectDetails")%>"'>Add lesson</button>
</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

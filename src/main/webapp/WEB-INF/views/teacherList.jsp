<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Teacher" %>
<%@ page import="org.example.controllers.PaginationController" %>
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
<ul class="pagination"><%=request.getAttribute("pagination")%></ul>
<table id="myTable">
    <tr>
        <th><input type="text" id="teacher" onkeyup="filter(id, 0)" class="filters"></th>
        <th></th>
        <th></th>
    </tr>
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
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

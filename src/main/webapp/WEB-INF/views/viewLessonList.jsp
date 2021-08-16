<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Lesson" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lessons list</title>
    <link rel="icon" type="img/png" href="../images/icon.png">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
    <div align="center" class="box">
<h2>Lessons list</h2>
<p>Class:<%=request.getAttribute("class")%></p>
<p>Subject:<%=request.getAttribute("teacher")%></p>
<p>Theme:<%=request.getAttribute("theme")%></p>
<%if(request.getAttribute("teacher") != null) {%>
<p>Teacher:<%=request.getAttribute("teacher")%></p>
<%}
    int themeID =(int) request.getAttribute("themeID");
    %>
<table id="myTable">
    <tr>
        <sec:authorize access="hasAuthority('ADMIN')">
        <th>ID</th>
        </sec:authorize>
        <th>Date</th>
        <th>Topic</th>
        <th></th>
        <sec:authorize access="hasAuthority('TEACHER')">
        <th></th>
        <th></th>
        <th></th>
        </sec:authorize>
    </tr>
    <tr>
        <%
            int i = 1;
        %>
        <sec:authorize access="hasAuthority('ADMIN')">
        <th><input type="text" id="id" onkeyup="filter(id, <%=i++%>)" class="search-slim"></th>
        </sec:authorize>
        <th><input type="text" id="date" onkeyup="filter(id, <%=i++%>)" class="search"></th>
        <th><input type="text" id="topic" onkeyup="filter(id, <%=i%>)" class="search"></th>
        <th></th>
        <sec:authorize access="hasAuthority('TEACHER')">
        <th></th>
        <th></th>
        <th></th>
        </sec:authorize>
    </tr>
    <tbody>
    <% for (Lesson lesson:(List<Lesson>)request.getAttribute("list")) { %>
    <tr>
        <sec:authorize access="hasAuthority('ADMIN')">
        <td><%=lesson.getId()%></td>
        </sec:authorize>
        <td><%=lesson.getDate()%></td>
        <td><%=lesson.getTopic()%></td>
        <td><a href="../viewMarksByLesson/<%=lesson.getId()%>">view marks</a></td>
        <sec:authorize access="hasAuthority('TEACHER')">
        <td><a href="../addMark/<%=lesson.getId()%>">add mark</a></td>
        <td><a href="../editLesson/<%=lesson.getId()%>">edit lesson</a></td>
        <td><a href="../deleteLesson/<%=lesson.getId()%>">delete lesson</a></td>
        </sec:authorize>
    </tr>
    <% } %>
    </tbody>
</table>
<br/>
<button onclick='location.href="../index.jsp"'>Menu</button>
<button onclick=history.back()>Back</button>
<sec:authorize access="hasAuthority('TEACHER')">
<button onclick='location.href="../addLesson/<%=themeID%>"'>Add</button>
</sec:authorize>
</div>
</div>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/filter.js"%>
</script>
</html>
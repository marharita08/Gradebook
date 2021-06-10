<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Lesson" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Lessons list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
<h2><%=request.getAttribute("header")%></h2>
<%if(request.getAttribute("teacher") != null) {%>
<p><%=request.getAttribute("teacher")%></p>
<%}
    int pageNum = (int)request.getAttribute("pageNum");
        String pagination = (String) request.getAttribute("pagination");
        int sd =(int) request.getAttribute("subjectDetails");
    %>
<ul class="pagination"><%=pagination%></ul>
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
            String searchFunc;
            int i = 0;
            if(pagination.equals("")) {
                searchFunc = "filter(id," + i++ + ")";
            } else {
                searchFunc = "search(id, " + pageNum + ", " + sd + ")";
            }
        %>
        <sec:authorize access="hasAuthority('ADMIN')">
        <th><input type="text" id="id" onkeyup="<%=searchFunc%>" class="slim"></th>
    <%
        if(pagination.equals("")) {
            searchFunc = "filter(id," + i++ + ")";
        }
    %>
        </sec:authorize>
        <th><input type="text" id="date" onkeyup="<%=searchFunc%>"></th>
        <%
            if(pagination.equals("")) {
                searchFunc = "filter(id," + i++ + ")";
            }
        %>
        <th><input type="text" id="topic" onkeyup="<%=searchFunc%>"></th>
        <th></th>
        <sec:authorize access="hasAuthority('TEACHER')">
        <th></th>
        <th></th>
        <th></th>
        </sec:authorize>
    </tr>
    <tbody id="placeToShow">
    <% for (Lesson lesson:(List<Lesson>)request.getAttribute("list")) { %>
    <tr>
        <sec:authorize access="hasAuthority('ADMIN')">
        <td><%=lesson.getId()%></td>
        </sec:authorize>
        <td><%=lesson.getDate()%></td>
        <td><%=lesson.getTopic()%></td>
        <td><a href="/Gradebook/viewMarksByLesson/<%=lesson.getId()%>">view marks</a></td>
        <sec:authorize access="hasAuthority('TEACHER')">
        <td><a href="/Gradebook/addMark/<%=lesson.getId()%>">add mark</a></td>
        <td><a href="/Gradebook/editLesson/<%=lesson.getId()%>">edit lesson</a></td>
        <td><a href="/Gradebook/deleteLesson/<%=lesson.getId()%>?page=<%=pageNum%>">delete lesson</a></td>
        </sec:authorize>
    </tr>
    <% } %>
    </tbody>
</table>
<br/>
<button onclick='location.href="/Gradebook/"'>Menu</button>
<button onclick=history.back()>Back</button>
<sec:authorize access="hasAuthority('TEACHER')">
<button onclick='location.href="/Gradebook/addLesson/<%=sd%>"'>Add</button>
</sec:authorize>
</div>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/searchLessons.js"%>
    <%@include file="../js/filter.js"%>
</script>
</html>

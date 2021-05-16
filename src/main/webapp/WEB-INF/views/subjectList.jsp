<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Subject" %>
<%@ page import="org.example.controllers.PaginationController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Subject list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<h2><%=request.getAttribute("header")%></h2>
<ul class="pagination"><%=request.getAttribute("pagination")%></ul>
<table id="myTable">
    <tr>
        <th><input type="text" id="subject" onkeyup="filter(id, 0)" class="filters"></th>
        <% if(request.getAttribute("param") == "class") {%>
        <th></th>
        <%}%>
        <th></th>
        <th></th>
        <th></th>
    </tr>

    <% for (Subject subject:(List<Subject>)request.getAttribute("list")) { %>
    <tr>
        <td>
            <%=subject.getName()%>
        </td>
        <% if(request.getAttribute("param") == "class") {%>
        <td>
            <a href="/Gradebook/viewLessonsByPupilClassAndSubject?page=1/<%=request.getAttribute("id")%>/<%=subject.getId()%>">
                view lessons</a>
        </td>
        <%}%>
        <td>
            <a href="/Gradebook/viewPupilClassesBySubject/<%=subject.getId()%>">
                view classes</a>
        </td>
        <td>
            <a href="/Gradebook/viewTeachersBySubject/<%=subject.getId()%>">
                view teachers</a>
        </td>
        <td>
            <a href="/Gradebook/viewSubjectDetailsBySubject/<%=subject.getId()%>">
                view class-teacher list</a>
        </td>
    </tr>
    <% } %>
</table>
<br/>
<button onclick='location.href="/Gradebook/index.jsp"'>Menu</button>
<button onclick=history.back()>Back</button>
</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Mark" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mark list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
<h2><%=request.getAttribute("header")%></h2>
<p>Subject:<%=request.getAttribute("subject")%></p>
<p>Teacher:<%=request.getAttribute("teacher")%></p>
<p>Date:<%=request.getAttribute("date")%></p>
<p>Topic:<%=request.getAttribute("topic")%></p>
<select OnChange="sortTable(value)">
    <option>Sort by</option>
    <option value="0">Pupil</option>
    <option value="1">Mark</option>
</select><br/><br/>
<table id="myTable">
    <tr>
        <th>Pupil</th>
        <th>Mark</th>
        <sec:authorize access="hasAuthority('TEACHER')">
        <th></th>
        <th></th>
        </sec:authorize>
    </tr>
    <tr>
        <th><input type="text" id="pupil" onkeyup="filter(id, 0)" class="filters"></th>
        <th><input type="text" id="mark" onkeyup="filter(id, 1)" class="filters"></th>
        <sec:authorize access="hasAuthority('TEACHER')">
        <th></th>
        <th></th>
        </sec:authorize>
    </tr>
    <% for (Pupil pupil:(List<Pupil>)request.getAttribute("pupilList")) { %>
    <tr>
        <td><%=pupil.getName()%></td>

            <% int i = 0;
                for (Mark mark:(List<Mark>)request.getAttribute("list")) {%>
                <%if(mark.getPupil().equals(pupil)) {
                   i++;%>
                <td><%=mark.getMark()%></td>
                <sec:authorize access="hasAuthority('TEACHER')">
                <td><a href="/Gradebook/editMark/<%=mark.getId()%>">edit mark</a></td>
                <td><a href="/Gradebook/deleteMark/<%=mark.getId()%>">delete mark</a></td>
                </sec:authorize>
            <% }}
            if(i == 0) { %>
                <td></td>
                <sec:authorize access="hasAuthority('TEACHER')">
                <td></td>
                <td></td>
                </sec:authorize>
            <%
            } %>

    </tr>
    <% } %>
</table>
<br/>
<button onclick='location.href="/Gradebook/"'>Menu</button>
<button onclick=history.back()>Back</button>
<sec:authorize access="hasAuthority('TEACHER')">
<button onclick='location.href="/Gradebook/addMark/<%=request.getAttribute("lesson")%>"'>Add mark</button>
</sec:authorize>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

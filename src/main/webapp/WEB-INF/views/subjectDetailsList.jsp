<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.SubjectDetails" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Subject details list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%if(request.getAttribute("header") != null) {%>
<h2><%=request.getAttribute("header")%></h2>
<%}%>
<table id="myTable">
    <tr>
        <th><%=request.getAttribute("tableHeader1")%></th>
        <th><%=request.getAttribute("tableHeader2")%></th>
        <th></th>
        <th></th>
        <th></th>
    </tr>
    <tr>
        <th><input type="text" id="0" onkeyup="filter(id, 0)" class="filters"></th>
        <th><input type="text" id="1" onkeyup="filter(id, 1)" class="filters"></th>
        <th></th>
        <th></th>
        <th></th>
    </tr>
    <% for (SubjectDetails subjectDetails:(List<SubjectDetails>)request.getAttribute("list")) { %>
    <tr>
        <%if(request.getAttribute("param") != "class") {%>
            <td><%=subjectDetails.getPupilClass().getName()%></td>
        <%}%>
        <%if(request.getAttribute("param") != "subject") {%>
            <td><%=subjectDetails.getSubject().getName()%></td>
        <%}%>
        <%if(request.getAttribute("param") != "teacher") {%>
            <td><%=subjectDetails.getTeacher().getName()%></td>
        <%}%>
        <td><a href="/Gradebook/viewLessonsBySubjectDetails/<%=subjectDetails.getId()%>?page=1">view lessons</a></td>
        <td><a href="/Gradebook/addLesson/<%=subjectDetails.getId()%>">add lesson</a></td>
        <td><a href="/Gradebook/viewMarksBySubjectDetails/<%=subjectDetails.getId()%>">view marks</a></td>
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


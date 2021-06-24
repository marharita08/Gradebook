<%@ page import="org.example.entities.Subject" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Mark" %>
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
<div align="center">
<h2><%=request.getAttribute("header")%></h2>
<table id="myTable">
    <tr>
        <th>Subject</th>
        <th>Marks</th>
    </tr>
    <tr>
        <th><input type="text" id="subject" onkeyup="filter(id, 0)"></th>
        <th></th>
    </tr>
    <% for (Subject subject:(List<Subject>)request.getAttribute("subjectList")) { %>
    <tr>
        <td><%=subject.getName()%></td>
        <td>
        <%for (Mark mark:(List<Mark>)request.getAttribute("list")) {%>
            <%=mark.getLesson().getSubjectDetails().getSubject().equals(subject)?mark.getMark() + " ":""%>
        <% } %>
        </td>
    </tr>
    <% } %>
</table>
<br/>
<button onclick='location.href="../index.jsp"'>Menu</button>
<button onclick=history.back()>Back</button>
</div>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/filter.js"%>
</script>
</html>

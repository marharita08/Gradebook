<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Class list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
    <h2><%=request.getAttribute("header")%></h2>
    <ul class="pagination"><%=request.getAttribute("pagination")%></ul>
    <table id="myTable">
        <tr>
            <th><input type="text" id="class" onkeyup="filter(id, 0)" class="filters"></th>
            <th></th>
            <th></th>
            <th></th>
            <th></th>
        </tr>
    <% for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("list")) { %>
        <tr>
            <td><%=pupilClass.getName()%></td>
            <td><a href="<%= "/Gradebook/viewPupilsByPupilClass/" + pupilClass.getId()%>">view pupil list</a></td>
            <td><a href="<%= "/Gradebook/viewSubjectsByPupilClass/" + pupilClass.getId()%>">view subjects</a></td>
            <td><a href="<%= "/Gradebook/viewTeachersByPupilClass/" + pupilClass.getId()%>">view teacher list</a></td>
            <td><a href="<%= "/Gradebook/viewSubjectDetailsByPupilClass/" + pupilClass.getId()%>">view teacher-subject list</a></td>
        </tr>
    <% } %>
    </table>
    <br/>
    <button onclick='location.href="/Gradebook/"'>Menu</button>
    <button onclick='history.back()'>Back</button>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

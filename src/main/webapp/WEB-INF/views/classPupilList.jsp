<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Pupil list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
    <h2>Pupils of <%=((PupilClass)request.getAttribute("class")).getName()%> form</h2>
    <ul class="pagination"><%=request.getAttribute("pagination")%></ul>
    <table id="myTable">
        <tr>
            <th><input type="text" id="pupil" onkeyup="filter(id, 0)" class="filters"></th>
            <th></th>
        </tr>
    <% for (Pupil pupil:(List<Pupil>)request.getAttribute("list")) { %>
        <tr>
            <td><%=pupil.getName()%></td>
            <td><a href="/Gradebook/viewMarksByPupil/<%=pupil.getId()%>">view marks</a></td>
        </tr>
    <% } %>
    </table>
    <br/>
    <button onclick='location.href="/Gradebook/"'>Menu</button>
    <button onclick='history.back()'>Back</button>
    <sec:authorize access="hasAuthority('ADMIN')">
    <button onclick='location.href="/Gradebook/addPupil/<%=((PupilClass)request.getAttribute("class")).getId()%>"'>Add pupil</button>
    </sec:authorize>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

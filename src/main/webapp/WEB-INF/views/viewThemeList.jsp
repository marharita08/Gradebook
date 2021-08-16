<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Theme" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Theme list</title>
    <link rel="icon" type="img/png" href="../images/icon.png">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
    <div align="center" class="box">
        <h2><%=request.getAttribute("header")%></h2>
        <%if(request.getAttribute("teacher") != null) {%>
        <p><%=request.getAttribute("teacher")%></p>
        <%}
            int sd =(int) request.getAttribute("subjectDetails");
        %>
        <table id="myTable">
            <tr>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <th>ID</th>
                </sec:authorize>
                <th>Name</th>
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
                <th><input type="text" id="name" onkeyup="filter(id, <%=i%>)" class="search"></th>
                <th></th>
                <sec:authorize access="hasAuthority('TEACHER')">
                    <th></th>
                    <th></th>
                    <th></th>
                </sec:authorize>
            </tr>
            <tbody>
            <% for (Theme theme:(List<Theme>)request.getAttribute("list")) { %>
            <tr>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <td><%=theme.getId()%></td>
                </sec:authorize>
                <td><%=theme.getName()%></td>
                <td><a href="../viewLessonsByTheme/<%=theme.getId()%>">view lessons</a></td>
                <sec:authorize access="hasAuthority('TEACHER')">
                    <td><a href="../addLesson/<%=theme.getId()%>">add lesson</a></td>
                    <td><a href="../editTheme/<%=theme.getId()%>">edit theme</a></td>
                    <td><a href="../deleteTheme/<%=theme.getId()%>">delete theme</a></td>
                </sec:authorize>
            </tr>
            <% } %>
            </tbody>
        </table>
        <br/>
        <button onclick='location.href="../index.jsp"'>Menu</button>
        <button onclick=history.back()>Back</button>
        <sec:authorize access="hasAuthority('TEACHER')">
            <button onclick='location.href="../addTheme/<%=sd%>"'>Add</button>
        </sec:authorize>
    </div>
</div>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/filter.js"%>
</script>
</html>


<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Lesson" %>
<%@ page import="org.example.entities.User" %>
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
<h2><%=request.getAttribute("header")%></h2>
<%if(request.getAttribute("teacher") != null) {%>
<p><%=request.getAttribute("teacher")%></p>
<%}
    int pageNum = (int)request.getAttribute("pageNum");
    String pagination = (String) request.getAttribute("pagination");
    int sd =(int) request.getAttribute("subjectDetails");
    boolean isAdmin = false;
    boolean isTeacher = false;
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (user.hasRole("ADMIN")) {
        isAdmin = true;
    }
    if (user.hasRole("TEACHER")) {
        isTeacher = true;
    }
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
    <%
        if (pageNum == 1) { %>
    <tr>
        <%
            String searchFunc;
            int i = 0;
            if(pagination.equals("")) {
                searchFunc = "filter(id," + i++ + ")";
            } else {
                searchFunc = "search(id, " + sd + ", " + isAdmin + ", " + isTeacher + ")";
            }
        %>
        <sec:authorize access="hasAuthority('ADMIN')">
        <th><input type="text" id="id" onkeyup="<%=searchFunc%>" class="search-slim"></th>
    <%
        if(pagination.equals("")) {
            searchFunc = "filter(id," + i++ + ")";
        }
    %>
        </sec:authorize>
        <th><input type="text" id="date" onkeyup="<%=searchFunc%>" class="search"></th>
        <%
            if(pagination.equals("")) {
                searchFunc = "filter(id," + i++ + ")";
            }
        %>
        <th><input type="text" id="topic" onkeyup="<%=searchFunc%>" class="search"></th>
        <th></th>
        <sec:authorize access="hasAuthority('TEACHER')">
        <th></th>
        <th></th>
        <th></th>
        </sec:authorize>
    </tr>
    <%}%>
    <tbody id="placeToShow">
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
        <td><a href="../deleteLesson/<%=lesson.getId()%>?page=<%=pageNum%>">delete lesson</a></td>
        </sec:authorize>
    </tr>
    <% } %>
    </tbody>
</table>
<br/>
<button onclick='location.href="../index.jsp"'>Menu</button>
<button onclick=history.back()>Back</button>
<sec:authorize access="hasAuthority('TEACHER')">
<button onclick='location.href="../addLesson/<%=sd%>"'>Add</button>
</sec:authorize>
</div>
</div>
<%@include file="footer.jsp"%>
</body>
<script>

    var request = new XMLHttpRequest();
    function search(param, sd, isAdmin, isTeacher) {
        var val = document.getElementById(param).value;
        var url = "../searchLessons?val=" + val + "&param=" + param + "&sd=" + sd;
        try {
            request.onreadystatechange = function () {
                if (request.readyState === 4) {
                    var obj = JSON.parse(request.responseText);
                    var result = "";
                    for (let i in obj) {
                        var id = obj[i].id;
                        result += "<tr>";
                        if (isAdmin === true) {
                            result += "<td>" + id + "</td>";
                        }
                        var fullDate = new Date(obj[i].date);
                        var year = fullDate.getFullYear();
                        var day = fullDate.getDate();
                        var month = fullDate.getMonth() + 1;
                        if (month  < 10) {
                            month  = "0" + month;
                        }
                        if (day < 10) {
                            day = "0" + day;
                        }
                        result += "<td>" + year + "-" + month + "-" + day + "</td>";
                        result += "<td>" + obj[i].topic + "</td>";
                        result += "<td>";
                        result +="<a href=\"../viewMarksByLesson/" + id + "\">view marks</a>";
                        result +="</td>";
                        if (isTeacher) {
                            result +="<td>";
                            result +="<a href=\"../addMark/" + id + "\">add mark</a>";
                            result +="</td>";
                            result +="<td>";
                            result +="<a href=\"../editLesson/" + id + "\">edit lesson</a>";
                            result +="</td><td>";
                            result +="<a href=\"../deleteLesson/" + id + "?page=1\">delete lesson</a>";
                            result +="</td>";
                        }
                        result +="</tr>";
                    }
                    document.getElementById("placeToShow").innerHTML = result;
                }
            }
            request.open("GET", url, true);
            request.send();

        } catch (e) {
            alert("Unable to connect to server");
        }
    }
    <%@include file="../js/filter.js"%>
</script>
</html>

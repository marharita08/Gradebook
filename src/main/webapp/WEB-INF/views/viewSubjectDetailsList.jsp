<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.SubjectDetails" %>
<%@ page import="org.example.entities.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<%
    String toRoot = (String) request.getAttribute("toRoot");
%>
<head>
    <title>Subject Details List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="icon" type="img/png" href="<%=toRoot%>images/icon.png">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
    <div align="center" class="box">
<h2 align="center"><%=request.getAttribute("header")%></h2>
    <%int pageNum = (int)request.getAttribute("pageNum");
        String pagination = (String) request.getAttribute("pagination");
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
            <%if(request.getAttribute("param") != "class") {%>
            <th>Class</th>
            <%}%>
            <%if(request.getAttribute("param") != "teacher") {%>
            <th>Teacher</th>
            <%}%>
            <%if(request.getAttribute("param") != "subject") {%>
            <th>Subject</th>
            <%}%>

            <sec:authorize access="hasAuthority('ADMIN')">
            <th>EDIT</th>
            <th>DELETE</th>
            </sec:authorize>
            <th></th>
            <sec:authorize access="hasAuthority('TEACHER')">
            <th></th>
            </sec:authorize>
            <th></th>
        </tr>
        <%if (pageNum == 1) { %>
        <tr>
            <%
                String searchFunc;
                int i = 0;
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                } else {
                    searchFunc = "search(id, " + isAdmin + ", " + isTeacher + ")";
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
            <%if(request.getAttribute("param") != "class") {%>
            <th><input type="text" id="class" onkeyup="<%=searchFunc%>" class="search-slim"></th>
            <%
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                }
            }%>
            <%if(request.getAttribute("param") != "teacher") {%>
            <th><input type="text" id="teacher" onkeyup="<%=searchFunc%>" class="search"></th>
            <%
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                }
            }%>
            <%if(request.getAttribute("param") != "subject") {%>
            <th><input type="text" id="subject" onkeyup="<%=searchFunc%>" class="search"></th>
            <%}%>
            <sec:authorize access="hasAuthority('ADMIN')">
            <th></th>
            <th></th>
            </sec:authorize>
            <th></th>
            <sec:authorize access="hasAuthority('TEACHER')">
            <th></th>
            </sec:authorize>
            <th></th>
        </tr>
        <%}%>
        <tbody id="placeToShow">
        <% for (SubjectDetails subjectDetails:(List<SubjectDetails>)request.getAttribute("list")) { %>
        <tr>
            <sec:authorize access="hasAuthority('ADMIN')">
            <td><%=subjectDetails.getId()%></td>
            </sec:authorize>
            <%if(request.getAttribute("param") != "class") {%>
            <td><%=subjectDetails.getPupilClass().getName()%></td>
            <%}%>
            <%if(request.getAttribute("param") != "teacher") {%>
            <% if(subjectDetails.getTeacher() != null) { %>
            <td><%=subjectDetails.getTeacher().getName()%></td>
            <% } else { %>
            <td>-</td>
            <%}
            }%>
            <%if(request.getAttribute("param") != "subject") {%>
            <td><%=subjectDetails.getSubject().getName()%></td>
            <%}%>
            <sec:authorize access="hasAuthority('ADMIN')">
            <td><a href="<%=toRoot%>editSubjectDetails/<%=subjectDetails.getId()%>">Edit</a></td>
            <td><a href="<%=toRoot%>deleteSubjectDetails/<%=subjectDetails.getId()%>?page=<%=pageNum%>">Delete</a></td>
            </sec:authorize>
            <td><a href="<%=toRoot%>viewLessonsBySubjectDetails/<%=subjectDetails.getId()%>?page=1">view lessons</a></td>
            <sec:authorize access="hasAuthority('TEACHER')">
                <td><a href="<%=toRoot%>addLesson/<%=subjectDetails.getId()%>">add lesson</a></td>
            </sec:authorize>
            <td><a href="<%=toRoot%>viewMarksBySubjectDetails/<%=subjectDetails.getId()%>">view marks</a></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <br/>
    <button onclick='location.href="<%=toRoot%>index.jsp"'>Menu</button>
    <button onclick=history.back()>Back</button>
    <sec:authorize access="hasAuthority('ADMIN')">
    <button onclick='location.href="<%=toRoot%>addSubjectDetails"'>Add</button>
    </sec:authorize>
</div>
</div>
<%@include file="footer.jsp"%>

</body>
<script>
    var request = new XMLHttpRequest();
    function search(param, isAdmin, isTeacher) {
        var val = document.getElementById(param).value;
        var url = "searchSubjectDetails?val=" + val + "&param=" + param;
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
                        result += "<td>" + obj[i].pupilClass.name + "</td>";
                        result += "<td>";
                        if (obj[i].teacher != null) {
                            result += obj[i].teacher.name;
                        } else {
                            result += "-";
                        }
                        result += "</td>";
                        result += "<td>" + obj[i].subject.name + "</td>";
                        if (isAdmin === true) {
                            result += "<td>";
                            result += "<a href=\"editSubjectDetails/" + id + "\">Edit</a>";
                            result += "</td><td>";
                            result += "<a href=\"deleteSubjectDetails/" + id + "?page=1\">Delete</a></td>";
                            result += "</td>";
                        }
                        result += "<td>";
                        result += "<a href=\"viewLessonsBySubjectDetails/" + id + "?page=1\">view lessons</a>";
                        result += "</td>";
                        if (isTeacher === true) {
                            result += "<td>";
                            result += "<a href=\"addLesson/" + id + "\">add lesson</a>";
                            result += "</td>";
                        }
                        result += "<td>";
                        result += "<a href=\"viewMarksBySubjectDetails/" + id + "\">view marks</a>";
                        result += "</td>";
                        result += "</tr>";
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

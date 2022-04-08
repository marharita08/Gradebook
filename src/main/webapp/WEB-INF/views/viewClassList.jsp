<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Class List</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
        <style><%@include file="../css/style.css"%></style>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <h2 align="center"><%=request.getAttribute("header")%></h2>
                <%
                    int pageNum = (int)request.getAttribute("pageNum");
                    String pagination = (String) request.getAttribute("pagination");
                    boolean isAdmin = currUser.hasRole("ADMIN");
                    boolean isTeacher = currUser.hasRole("TEACHER");
                %>
                <ul class="pagination"><%=pagination%></ul>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                        </sec:authorize>
                        <th>Grade</th>
                        <th>Name</th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>EDIT</th>
                            <th>DELETE</th>
                        </sec:authorize>
                        <sec:authorize access="hasAnyAuthority('ADMIN', 'TEACHER')">
                            <th></th>
                        </sec:authorize>
                        <th></th>
                    </tr>
                    <%
                        if (pageNum == 1) {
                    %>
                            <tr>
                                <%
                                    String searchFunc;
                                    int i = 0;
                                    if(pagination.equals("")) {
                                        searchFunc = "filter(id," + i++ + ")";
                                    } else {
                                        searchFunc = "search(id, " + isAdmin + "," + isTeacher + ")";
                                    }
                                %>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th>
                                        <input type="text" id="id" onkeyup="<%=searchFunc%>" class="search-slim">
                                    </th>
                                    <%
                                        if(pagination.equals("")) {
                                            searchFunc = "filter(id," + i++ + ")";
                                        }
                                    %>
                                </sec:authorize>
                                    <th>
                                        <input type="text" id="grade" onkeyup="<%=searchFunc%>" class="search-slim">
                                    </th>
                                    <%
                                        if(pagination.equals("")) {
                                            searchFunc = "filter(id," + i + ")";
                                        }
                                    %>
                                <th><input type="text" id="name" onkeyup="<%=searchFunc%>" class="search-slim"></th>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th></th>
                                    <th></th>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ADMIN', 'TEACHER')">
                                    <th></th>
                                </sec:authorize>
                                <th></th>
                            </tr>
                    <%
                        }
                    %>
                    <tbody id="placeToShow">
                        <%
                            for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("list")) {
                        %>
                            <tr>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <td><%=pupilClass.getId()%></td>
                                </sec:authorize>
                                <td><%=pupilClass.getGrade()%></td>
                                <td><%=pupilClass.getName()%></td>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <td><a href="<%=root%>class/<%=pupilClass.getId()%>">Edit</a></td>
                                    <td>
                                        <a href="<%=root%>class/<%=pupilClass.getId()%>/delete?page=<%=pageNum%>">
                                            Delete
                                        </a>
                                    </td>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('ADMIN', 'TEACHER')">
                                    <td>
                                        <a href="<%=root%>class/<%=pupilClass.getId()%>/pupils">
                                            view pupils
                                        </a>
                                    </td>
                                </sec:authorize>
                                <td>
                                    <a href="<%=root%>class/<%= pupilClass.getId()%>/subject-details">
                                        view subjects
                                    </a>
                                </td>
                            </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
                <br/>
                <button onclick='location.href="<%=root%>index.jsp"'>Menu</button>
                <button onclick='history.back()'>Back</button>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <button onclick='location.href="<%=root%>class"'>Add</button>
                </sec:authorize>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        var request = new XMLHttpRequest();
        function search(param, isAdmin, isTeacher) {
            var val = document.getElementById(param).value;
            var url = "classes/search?val=" + val + "&param=" + param;
            try {
                request.onreadystatechange = function () {
                    if (request.readyState === 4) {
                        var obj = JSON.parse(request.responseText);
                        var result = "";
                        for (let i in obj) {
                            var id = obj[i].id;
                            result += "<tr>";
                            if (isAdmin) {
                                result += "<td>" + id + "</td>";
                            }
                            result += "<td>" + obj[i].grade + "</td>";
                            result += "<td>" + obj[i].name + "</td>";
                            if (isAdmin) {
                                result += "<td>";
                                result += "<a href=\"class/" + id + "\">Edit</a>";
                                result += "</td><td>";
                                result += "<a href=\"class/" + id + "delete?page=1\">Delete</a></td>";
                                result += "</td>";
                            }
                            if (isAdmin || isTeacher) {
                                result += "<td>";
                                result += "<a href=\"class/" + id + "pupils\">view pupils</a>";
                                result += "</td>";
                            }
                            result +=  "<td>";
                            result += "<a href=\"class/" + id + "subject-details\">view subjects</a>";
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
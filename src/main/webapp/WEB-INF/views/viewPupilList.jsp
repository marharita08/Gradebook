<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <%
        String toRoot1 = (String) request.getAttribute("toRoot");
    %>
    <head>
        <title>Pupil List</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="icon" type="img/png" href="<%=toRoot1%>images/icon.png">
        <style><%@include file="../css/style.css"%></style>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <h2 align="center"><%=request.getAttribute("header")%> </h2>
                <%
                    int pageNum = (int)request.getAttribute("pageNum");
                    String pagination = (String) request.getAttribute("pagination");
                    boolean isAdmin = false;
                    if (currUser.hasRole("ADMIN")) {
                        isAdmin = true;
                    }
                %>
                <ul class="pagination"><%=pagination%></ul>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                            <th>Class</th>
                        </sec:authorize>
                        <th>Name</th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>EDIT</th>
                            <th>DELETE</th>
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
                                        searchFunc = "search(id, " + isAdmin + ")";
                                    }
                                %>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th><input type="text" id="id" onkeyup="<%=searchFunc%>" class="search-slim"></th>
                                    <%
                                        if(pagination.equals("")) {
                                            searchFunc = "filter(id," + i++ + ")";
                                        }
                                    %>
                                    <th><input type="text" id="class" onkeyup="<%=searchFunc%>" class="search-slim"></th>
                                    <%
                                        if(pagination.equals("")) {
                                            searchFunc = "filter(id," + i + ")";
                                        }
                                    %>
                                </sec:authorize>
                                <th><input type="text" id="name" onkeyup="<%=searchFunc%>" class="search"></th>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th></th>
                                    <th></th>
                                </sec:authorize>
                                <th></th>
                            </tr>
                    <%
                        }
                    %>
                    <tbody id="placeToShow">
                        <%
                            for (Pupil pupil:(List<Pupil>)request.getAttribute("list")) {
                        %>
                                <tr>
                                    <sec:authorize access="hasAuthority('ADMIN')">
                                        <td><%=pupil.getId()%></td>
                                        <%
                                            if(pupil.getPupilClass() != null) {
                                        %>
                                                <td><%=pupil.getPupilClass().getName()%></td>
                                        <%
                                            } else {
                                        %>
                                                <td>-</td>
                                        <%
                                            }
                                        %>
                                    </sec:authorize>
                                    <td><%=pupil.getName()%></td>
                                    <sec:authorize access="hasAuthority('ADMIN')">
                                        <td><a href="<%=toRoot%>editPupil/<%=pupil.getId()%>">Edit</a></td>
                                        <td>
                                            <a href="<%=toRoot%>deletePupil/<%=pupil.getId()%>?page=<%=pageNum%>">
                                                Delete
                                            </a>
                                        </td>
                                    </sec:authorize>
                                    <td>
                                        <%
                                            if((currUser.hasRole("ADMIN") || currUser.hasRole("TEACHER")
                                                    || currUser.getId() == pupil.getId())) {
                                        %>
                                                <a href="<%=toRoot%>viewMarksByPupil/<%=pupil.getId()%>">view marks</a>
                                        <%
                                            }
                                        %>
                                    </td>
                                </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
                <br/>
                <button onclick='location.href="<%=toRoot%>index.jsp"'>Menu</button>
                <button onclick='history.back()'>Back</button>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <button onclick='location.href="<%=toRoot%>addPupil"'>Add</button>
                </sec:authorize>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        var request = new XMLHttpRequest();
        function search(param, isAdmin) {
            var val = document.getElementById(param).value;
            var url = "searchPupils?val=" + val + "&param=" + param;
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
                                result += "<td>";
                                if (obj[i].pupilClass != null) {
                                    result += obj[i].pupilClass.name;
                                } else {
                                    result += "-";
                                }
                                result += "</td>";
                            }
                            result += "<td>" + obj[i].name + "</td>";
                            if (isAdmin === true) {
                                result += "<td>";
                                result += "<a href=\"editPupil/" + id + "\">Edit</a>";
                                result += "</td><td>";
                                result += "<a href=\"deletePupil/" + id + "?page=1\">Delete</a></td>";
                                result += "</td>";
                            }
                            result += "<td>";
                            result += "<a href=\"viewMarksByPupil/" + id + "\">view marks</a>";
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
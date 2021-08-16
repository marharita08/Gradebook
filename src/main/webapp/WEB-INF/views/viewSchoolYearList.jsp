<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.User" %>
<%@ page import="org.example.entities.SchoolYear" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<%
    String toRoot = (String) request.getAttribute("toRoot");
%>
<head>
    <title>School Years List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <link rel="icon" type="img/png" href="<%=toRoot%>images/icon.png">
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
    <div align="center" class="box">
        <h2 align="center"><%=request.getAttribute("header")%></h2>
        <%int pageNum = (int)request.getAttribute("pageNum");
            String pagination = (String) request.getAttribute("pagination");
            boolean isAdmin = false;
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user.hasRole("ADMIN")) {
                isAdmin = true;
            }
        %>
        <ul class="pagination"><%=pagination%></ul>
        <table id="myTable">
            <tr>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <th>ID</th>
                </sec:authorize>
                <th>Name</th>
                <th>Start date</th>
                <th>End date</th>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <th>EDIT</th>
                    <th>DELETE</th>
                </sec:authorize>
            </tr>
            <%if (pageNum == 1) { %>
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
                </sec:authorize>

                <th><input type="text" id="name" onkeyup="<%=searchFunc%>" class="search"></th>
                <%
                    if(pagination.equals("")) {
                        searchFunc = "filter(id," + i++ + ")";
                    }
                %>
                <th><input type="text" id="startDate" onkeyup="<%=searchFunc%>" class="search"></th>
                <%
                    if(pagination.equals("")) {
                        searchFunc = "filter(id," + i++ + ")";
                    }
                %>
                <th><input type="text" id="endDate" onkeyup="<%=searchFunc%>" class="search"></th>

                <sec:authorize access="hasAuthority('ADMIN')">
                    <th></th>
                    <th></th>
                </sec:authorize>
            </tr>
            <%}%>
            <tbody id="placeToShow">
            <%
                for (SchoolYear schoolYear:(List<SchoolYear>)request.getAttribute("list")) {
            %>
            <tr>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <td><%=schoolYear.getId()%></td>
                </sec:authorize>
                <td><%=schoolYear.getName()%></td>
                <td><%=schoolYear.getStartDate()%></td>
                <td><%=schoolYear.getEndDate()%></td>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <td><a href="<%=toRoot%>editSchoolYear/<%=schoolYear.getId()%>">Edit</a></td>
                    <td><a href="<%=toRoot%>deleteSchoolYear/<%=schoolYear.getId()%>?page=<%=pageNum%>">Delete</a></td>
                </sec:authorize>
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
            <button onclick='location.href="<%=toRoot%>addSchoolYear"'>Add</button>
        </sec:authorize>
    </div>
</div>
<%@include file="footer.jsp"%>
</body>
<script>

    var request = new XMLHttpRequest();
    function search(param, isAdmin) {
        var val = document.getElementById(param).value;
        var url = "searchSchoolYears?val=" + val + "&param=" + param;
        try {
            request.onreadystatechange = function () {
                if (request.readyState === 4) {
                    var obj = JSON.parse(request.responseText);
                    var result = "";
                    for (let i in obj) {
                        result += "<tr>";
                        if (isAdmin === true) {
                            result += "<td>" + obj[i].id + "</td>";
                        }
                        result += "<td>" + obj[i].name + "</td>";
                        result += "<td>" + obj[i].startDate + "</td>";
                        result += "<td>" + obj[i].endDate + "</td>";
                        if (isAdmin === true) {
                            var id = obj[i].id;
                            result += "<td>";
                            result += "<a href=\"editSchoolYear/" + id + "\">Edit</a>";
                            result += "</td><td>";
                            result += "<a href=\"deleteSchoolYear/" + id + "?page=1\">Delete</a></td>";
                            result += "</td>";
                        }
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
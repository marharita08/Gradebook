<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Subject" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Subject List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="icon" type="img/png" href="images/icon.png">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
    <div align="center" class="box">
<h2 align="center"><%=request.getAttribute("header")%></h2>

    <% int pageNum = (int)request.getAttribute("pageNum");
        String pagination = (String) request.getAttribute("pagination");
        String toRoot = (String) request.getAttribute("toRoot");%>
    <ul class="pagination"><%=pagination%></ul>
    <table id="myTable">
        <tr>
            <sec:authorize access="hasAuthority('ADMIN')">
            <th>ID</th>
            </sec:authorize>
            <th>Name</th>
            <sec:authorize access="hasAuthority('ADMIN')">
            <th>EDIT</th>
            <th>DELETE</th>
            </sec:authorize>
            <th></th>
            <th></th>
            <th></th>
        </tr>
        <tr>
            <%
                String searchFunc;
                int i = 0;
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                } else {
                    searchFunc = "search(id, " + pageNum + ", 'searchSubjects')";
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
                <sec:authorize access="hasAuthority('ADMIN')">
            <th></th>
            <th></th>
                </sec:authorize>
            <th></th>
            <th></th>
            <th></th>
        </tr>
<tbody id="placeToShow">
        <% for (Subject subject:(List<Subject>)request.getAttribute("list")) { %>
        <tr>
            <sec:authorize access="hasAuthority('ADMIN')">
            <td><%=subject.getId()%></td>
            </sec:authorize>
            <td><%=subject.getName()%></td>
            <sec:authorize access="hasAuthority('ADMIN')">
            <td><a href="<%=toRoot%>editSubject/<%=subject.getId()%>">Edit</a></td>
            <td><a href="<%=toRoot%>deleteSubject/<%=subject.getId()%>?page=<%=pageNum%>">Delete</a></td>
            </sec:authorize>
            <td>
                <a href="<%=toRoot%>viewPupilClassesBySubject/<%=subject.getId()%>">
                    view classes</a>
            </td>
            <td>
                <a href="<%=toRoot%>viewTeachersBySubject/<%=subject.getId()%>">
                    view teachers</a>
            </td>
            <td>
                <a href="<%=toRoot%>viewSubjectDetailsBySubject/<%=subject.getId()%>">
                    view class-teacher list</a>
            </td>
        </tr>
        <% } %>
</tbody>
    </table>
    <br/>
    <button onclick='location.href="<%=toRoot%>index.jsp"'>Menu</button>
    <button onclick='history.back()'>Back</button>
    <sec:authorize access="hasAuthority('ADMIN')">
    <button onclick='location.href="<%=toRoot%>addSubject"'>Add</button>
    </sec:authorize>
</div>
</div>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/search.js"%>
    <%@include file="../js/filter.js"%>
</script>
</html>
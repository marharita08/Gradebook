<%@ page import="org.example.entities.Teacher" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Teacher List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <link rel="icon" type="img/png" href="images/icon.png">
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
<div align="center" class="box">
    <h2 align="center"><%=request.getAttribute("header")%></h2>
    <%int pageNum = (int)request.getAttribute("pageNum");
        String pagination = (String) request.getAttribute("pagination");
        String toRoot = (String) request.getAttribute("toRoot");
    %>
    <ul class="pagination"><%=pagination%></ul>
<table id="myTable">
    <tr>
        <sec:authorize access="hasAuthority('ADMIN')">
        <th>ID</th>
        </sec:authorize>
        <th>Name</th>
        <th>Position</th>
        <sec:authorize access="hasAuthority('ADMIN')">
        <th>Chief</th>
        <th>EDIT</th>
        <th>DELETE</th>
        </sec:authorize>
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
                searchFunc = "search(id, " + pageNum + ", 'searchTeachers')";
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

        <th><input type="text" id="name" onkeyup="<%=searchFunc%>" class="search-middle"></th>
        <%
            if(pagination.equals("")) {
                searchFunc = "filter(id," + i++ + ")";
            }
        %>
        <th><input type="text" id="position" onkeyup="<%=searchFunc%>" class="search-middle"></th>

        <sec:authorize access="hasAuthority('ADMIN')">
            <%
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                }
            %>
        <th><input type="text" id="chief" onkeyup="<%=searchFunc%>" class="search-middle"></th>
         <th></th>
        <th></th>
        </sec:authorize>
        <th></th>
        <th></th>
    </tr>
<tbody id="placeToShow">
<%
        for (Teacher teacher:(List<Teacher>)request.getAttribute("list")) {
%>
<tr>
    <sec:authorize access="hasAuthority('ADMIN')">
    <td><%=teacher.getId()%></td>
    </sec:authorize>
    <td><%=teacher.getName()%></td>
    <td><%=teacher.getPosition()%></td>
    <sec:authorize access="hasAuthority('ADMIN')">
    <% if(teacher.getChief() != null) { %>
    <td><%=teacher.getChief().getName()%></td>
    <% } else { %>
    <td>-</td>
    <%}%>
    <td><a href="<%=toRoot%>editTeacher/<%=teacher.getId()%>">Edit</a></td>
    <td><a href="<%=toRoot%>deleteTeacher/<%=teacher.getId()%>?page=<%=pageNum%>">Delete</a></td>
    </sec:authorize>
    <td><a href="<%=toRoot%>viewSubjectsByTeacher/<%=teacher.getId()%>">view subjects</a></td>
    <td><a href="<%=toRoot%>viewSubjectDetailsByTeacher/<%=teacher.getId()%>">view subject-class list</a></td>
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
    <button onclick='location.href="<%=toRoot%>addTeacher"'>Add</button>
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

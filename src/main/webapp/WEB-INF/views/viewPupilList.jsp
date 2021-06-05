<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Pupil List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<%@include file="header.jsp"%>
<h2 align="center"><%=request.getAttribute("header")%> </h2>
<div align="center">

    <%int pageNum = (int)request.getAttribute("pageNum");
        String pagination = (String) request.getAttribute("pagination");
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
        <tr>
            <%
                String searchFunc;
                int i = 0;
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                } else {
                    searchFunc = "search(id, " + pageNum + ", 'searchPupils')";
                }
            %>
            <sec:authorize access="hasAuthority('ADMIN')">
            <th><input type="text" id="id" onkeyup="<%=searchFunc%>" class="filters"></th>
                <%
                    if(pagination.equals("")) {
                        searchFunc = "filter(id," + i++ + ")";
                    }
                %>
            <th><input type="text" id="class" onkeyup="<%=searchFunc%>" class="filters"></th>
                <%
                    if(pagination.equals("")) {
                        searchFunc = "filter(id," + i++ + ")";
                    }
                %>
            </sec:authorize>
            <th><input type="text" id="name" onkeyup="<%=searchFunc%>" class="filters"></th>
            <sec:authorize access="hasAuthority('ADMIN')">
            <th></th>
            <th></th>
            </sec:authorize>
            <th></th>
        </tr>
        <tbody id="placeToShow">
        <% for (Pupil pupil:(List<Pupil>)request.getAttribute("list")) { %>
        <tr>
            <sec:authorize access="hasAuthority('ADMIN')">
            <td><%=pupil.getId()%></td>
            <% if(pupil.getPupilClass() != null) { %>
                <td><%=pupil.getPupilClass().getName()%></td>
            <% } else { %>
                <td>-</td>
            <%}%>
            </sec:authorize>
            <td><%=pupil.getName()%></td>
            <sec:authorize access="hasAuthority('ADMIN')">
            <td><a href="editPupil/<%=pupil.getId()%>">Edit</a></td>
            <td><a href="deletePupil/<%=pupil.getId()%>?page=<%=pageNum%>">Delete</a></td>
            </sec:authorize>
            <td><a href="/Gradebook/viewMarksByPupil/<%=pupil.getId()%>">view marks</a></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <br/>
    <button onclick='location.href="/Gradebook/"'>Menu</button>
    <button onclick='history.back()'>Back</button>
    <sec:authorize access="hasAuthority('ADMIN')">
    <button onclick='location.href="/Gradebook/addPupil"'>Add</button>
    </sec:authorize>
</div>
<%@include file="footer.jsp"%>

</body>
<script>
    <%@include file="../js/search.js"%>
    <%@include file="../js/filter.js"%>
</script>
</html>
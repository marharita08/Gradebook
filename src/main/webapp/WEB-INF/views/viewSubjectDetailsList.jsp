<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.SubjectDetails" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Subject Details List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<%@include file="header.jsp"%>
<h2 align="center"><%=request.getAttribute("header")%></h2>
<div align="center">
    <%int pageNum = (int)request.getAttribute("pageNum");
        String pagination = (String) request.getAttribute("pagination");
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
        <tr>
            <%
                String searchFunc;
                int i = 0;
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                } else {
                    searchFunc = "search(id, " + pageNum + ", 'searchSubjectDetails')";
                }
            %>
            <sec:authorize access="hasAuthority('ADMIN')">
            <th><input type="text" id="id" onkeyup="<%=searchFunc%>" class="slim"></th>
    <%
        if(pagination.equals("")) {
            searchFunc = "filter(id," + i++ + ")";
        }
    %>
            </sec:authorize>
            <%if(request.getAttribute("param") != "class") {%>
            <th><input type="text" id="class" onkeyup="<%=searchFunc%>" class="slim"></th>
            <%
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                }
            }%>
            <%if(request.getAttribute("param") != "teacher") {%>
            <th><input type="text" id="teacher" onkeyup="<%=searchFunc%>"></th>
            <%
                if(pagination.equals("")) {
                    searchFunc = "filter(id," + i++ + ")";
                }
            }%>
            <%if(request.getAttribute("param") != "subject") {%>
            <th><input type="text" id="subject" onkeyup="<%=searchFunc%>"></th>
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
            <td><a href="editSubjectDetails/<%=subjectDetails.getId()%>">Edit</a></td>
            <td><a href="deleteSubjectDetails/<%=subjectDetails.getId()%>?page=<%=pageNum%>">Delete</a></td>
            </sec:authorize>
            <td><a href="/Gradebook/viewLessonsBySubjectDetails/<%=subjectDetails.getId()%>?page=1">view lessons</a></td>
            <sec:authorize access="hasAuthority('TEACHER')">
                <td><a href="/Gradebook/addLesson/<%=subjectDetails.getId()%>">add lesson</a></td>
            </sec:authorize>
            <td><a href="/Gradebook/viewMarksBySubjectDetails/<%=subjectDetails.getId()%>">view marks</a></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <br/>
    <button onclick='location.href="/Gradebook/"'>Menu</button>
    <button onclick=history.back()>Back</button>
    <sec:authorize access="hasAuthority('ADMIN')">
    <button onclick='location.href="/Gradebook/addSubjectDetails"'>Add</button>
    </sec:authorize>
</div>
<%@include file="footer.jsp"%>

</body>
<script>
    <%@include file="../js/search.js"%>
    <%@include file="../js/filter.js"%>
</script>
</html>

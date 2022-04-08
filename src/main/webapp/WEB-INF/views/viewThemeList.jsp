<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Theme" %>
<%@ page import="org.example.entities.SubjectDetails" %>
<%@ page import="org.example.entities.Teacher" %>
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
                <%
                    SubjectDetails subjectDetails = (SubjectDetails) request.getAttribute("subjectDetails");
                    Teacher teacher = subjectDetails.getTeacher();
                    if(teacher != null) {
                %>
                        <p>Teacher:<%=teacher.getName()%></p>
                <%
                    }
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
                        <%
                            for (Theme theme:(List<Theme>)request.getAttribute("list")) {
                        %>
                                <tr>
                                    <sec:authorize access="hasAuthority('ADMIN')">
                                        <td><%=theme.getId()%></td>
                                    </sec:authorize>
                                    <td><%=theme.getName()%></td>
                                    <td><a href="../../theme/<%=theme.getId()%>/lessons">view lessons</a></td>
                                    <sec:authorize access="hasAuthority('TEACHER')">
                                        <%
                                            if(teacher != null && currUser.getId() == teacher.getId()) {
                                        %>
                                        <td><a href="../../theme/<%=theme.getId()%>/lesson">add lesson</a></td>
                                        <td><a href="../../theme/<%=theme.getId()%>">edit theme</a></td>
                                        <td><a href="../../theme/<%=theme.getId()%>/delete">delete theme</a></td>
                                        <%
                                            }
                                        %>
                                    </sec:authorize>
                                </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
                <br/>
                <button onclick='location.href="../index.jsp"'>Menu</button>
                <button onclick=history.back()>Back</button>
                <sec:authorize access="hasAuthority('TEACHER')">
                    <%
                        if(teacher != null && currUser.getId() == teacher.getId()) {
                    %>
                            <button onclick='location.href="../../teacher/<%=teacher.getId()%>/subject-details"'>
                                To subjects
                            </button>
                            <button onclick='location.href="../../subject-details/<%=subjectDetails.getId()%>/theme"'>Add</button>
                    <%
                        }
                    %>
                </sec:authorize>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/filter.js"%>
    </script>
</html>


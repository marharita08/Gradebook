<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.example.entities.User" %>
<header>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <%
        User currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String toRoot = (String) request.getAttribute("toRoot");
        if (toRoot == null) {
            toRoot = "";
        }
    %>
    <nav class="navbar-fixed-top  bg-primary">
        <ul>
            <li><a href="/Gradebook/" class="fa fa-home">Home</a></li>
            <sec:authorize access="hasAuthority('PUPIL')">
                <li><a href="<%=toRoot%>viewPupilsByPupil/<%=currUser.getId()%>" class="fa fa-users">My class</a></li>
                <li><a href="<%=toRoot%>viewSubjectDetailsByPupil/<%=currUser.getId()%>" class="fa fa-leanpub">My subjects</a></li>
                <li><a href="<%=toRoot%>viewMarksByPupil/<%=currUser.getId()%>" class="fa fa-edit">My marks</a></li>
            </sec:authorize>
            <sec:authorize access="hasAuthority('TEACHER')">
                <li><a href="<%=toRoot%>viewPupilClassesByTeacher/<%=currUser.getId()%>" class="fa fa-users">My classes</a></li>
                <li><a href="<%=toRoot%>viewSubjectDetailsByTeacher/<%=currUser.getId()%>" class="fa fa-leanpub">My subjects</a></li>
            </sec:authorize>
            <sec:authorize access="hasAuthority('ADMIN')">
                <li><a href="<%=toRoot%>viewAllUsers?page=1" class="fa fa-users">All users</a></li>
                <li><a href="<%=toRoot%>viewAllTeachers?page=1" class="fa fa-graduation-cap">All Teachers</a></li>
                <li><a href="<%=toRoot%>viewAllSubjects?page=1" class="fa fa-leanpub">All Subjects</a></li>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <li class="logout">
                    <a href="<%=toRoot%>userPage" class="fa fa-user username"><%=" " + currUser.getUsername() + " "%></a>
                    <a href="<%=toRoot%>logout" class="fa fa-sign-out">Logout</a></li>
            </sec:authorize>
        </ul>
    </nav>
    <br/>
    <br/>
</header>

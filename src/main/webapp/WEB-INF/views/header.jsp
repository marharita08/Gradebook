<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<header>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <nav class="navbar-fixed-top  bg-primary">
        <ul>
            <li><a href="/Gradebook/" class="fa fa-home">Home</a></li>
            <li><a href="/Gradebook/viewAllClasses?page=1" class="fa fa-users">Class list</a></li>
            <li><a href="/Gradebook/viewAllTeachers?page=1" class="fa fa-graduation-cap">Teachers list</a></li>
            <li><a href="/Gradebook/viewAllSubjects?page=1" class="fa fa-leanpub">Subject list</a></li>
            <sec:authorize access="isAuthenticated()">
            <li class="logout fa fa-user"><%=" " + SecurityContextHolder.getContext().getAuthentication().getName() + " "%>
                <a href="/Gradebook/logout" class="fa fa-sign-out">Logout</a></li>
            </sec:authorize>
        </ul>
    </nav>
    <br/><br/><br/>
</header>

<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
    <head>
        <title>Home</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="/WEB-INF/css/style.css"%></style>
        <link rel="icon" type="img/png" href="images/icon.png">
    </head>
    <body>
        <%@include file="WEB-INF/views/header.jsp"%>
        <div align="center">
            <div class="box box-index" align="left">
                <div class="menu">
                    <h2>Menu: </h2>
                    <nav class="push">
                        <sec:authorize access="hasAuthority('PUPIL')">
                            <a href="<%=root%>pupil/<%=currUser.getId()%>/pupils">My class</a>
                            <a href="<%=root%>pupil/<%=currUser.getId()%>/subject-details">My subjects</a>
                            <a href="<%=root%>pupil/<%=currUser.getId()%>/marks">My marks</a>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <a href="<%=root%>teacher/<%=currUser.getId()%>/classes">My classes</a>
                            <a href="<%=root%>teacher/<%=currUser.getId()%>/subject-details">My subjects</a>
                        </sec:authorize>
                        <a href="teachers?page=1">All teachers</a>
                        <a href="classes?page=1">All classes</a>
                        <a href="subjects?page=1">All subjects</a>
                        <a href="semesters?page=1">All Semesters</a>
                        <a href="years?page=1">All School Years</a>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <a href="subject-details?page=1">All subject details</a>
                            <a href="pupils?page=1">All pupils</a>
                            <a href="users?page=1">All users</a>
                        </sec:authorize>
                    </nav>
                </div>
                <div align="center" class="school-div">
                    <figure>
                        <img src="images/school.jpg" class="school">
                        <figcaption>Full name of the school</figcaption>
                    </figure>
                </div>
            </div>
        </div>
        <%@include file="WEB-INF/views/footer.jsp"%>
    </body>
</html>


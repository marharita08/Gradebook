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

            <a href="viewAllTeachers?page=1">View all teachers</a>
            <a href="viewAllClasses?page=1">View all classes</a>
            <a href="viewAllSubjects?page=1">View all subjects</a>
            <a href="viewAllSubjectDetails?page=1">View all subject details</a>
            <sec:authorize access="hasAuthority('ADMIN')">
                <a href="viewAllPupils?page=1">View all pupils</a>
                <a href="viewAllUsers?page=1">View all users</a>
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


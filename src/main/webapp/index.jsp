<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <title>Welcome page</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="/WEB-INF/css/style.css"%></style>
</head>
<body>
<%@include file="WEB-INF/views/header.jsp"%>
<h2>Menu: </h2>
<ul>

    <li><a href="/Gradebook/viewAllTeachers?page=1">View all teachers</a></li>
    <li><a href="/Gradebook/viewAllClasses?page=1">View all classes</a></li>
    <li><a href="/Gradebook/viewAllSubjects?page=1">View all subjects</a></li>
    <li><a href="/Gradebook/viewAllSubjectDetails?page=1">View all subject details</a></li>
    <sec:authorize access="hasAuthority('ADMIN')">
    <li><a href="/Gradebook/viewAllPupils?page=1">View all pupils</a></li>
    <li><a href="/Gradebook/viewAllUsers?page=1">View all users</a></li>
    </sec:authorize>
</ul>
<%@include file="WEB-INF/views/footer.jsp"%>
</body>
</html>


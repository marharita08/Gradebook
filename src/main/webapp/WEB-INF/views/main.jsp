<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.entities.School" %>
<html>
    <head>
        <title>Home</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="/WEB-INF/css/style.css"%></style>
        <link rel="icon" type="img/png" href="images/icon.png">
    </head>
    <body>
        <%@include file="header.jsp"%>
        <%
            School currSchool = (School)request.getAttribute("school");
        %>
        <div align="center">
            <div class="box box-index" align="left">
                <div class="menu">
                    <h2>Меню: </h2>
                    <nav class="push">
                        <sec:authorize access="hasAuthority('PUPIL')">
                            <a href="<%=root%>pupil/<%=currUser.getId()%>/pupils">Мій клас</a>
                            <a href="<%=root%>pupil/<%=currUser.getId()%>/subject-details">Мої предмети</a>
                            <a href="<%=root%>pupil/<%=currUser.getId()%>/marks">Мої оцінки</a>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <a href="<%=root%>teacher/<%=currUser.getId()%>/classes">Мої класи</a>
                            <a href="<%=root%>teacher/<%=currUser.getId()%>/subject-details">Мої предмети</a>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('ADMIN')">
                             <a href="users?page=1">Всі користувачі</a>
                             <a href="pupils?page=1">Всі учні</a>
                        </sec:authorize>
                        <sec:authorize access="hasAnyAuthority('ADMIN', 'TEACHER', 'PUPIL')">
                            <a href="teachers?page=1">Всі вчителі</a>
                            <a href="classes?page=1">Всі класи</a>
                            <a href="subjects?page=1">Всі предмети</a>
                        </sec:authorize>
                        <a href="years?page=1">Всі навчальні роки</a>
                        <a href="semesters?page=1">Всі семестри</a>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <a href="subject-details?page=1">Всі деталі предметів</a>
                        </sec:authorize>
                    </nav>
                </div>
                <div align="center" class="school-div">
                    <figure>
                        <img src='<%=currSchool.getPhoto()!=null?currSchool.getPhoto():"images/school.jpg"%>' class="school">
                        <figcaption>
                            <div class="inline">
                                <%=currSchool.getName() + " "%>
                            </div>
                            <div class="inline">
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <a href='<%=root%>school/<%=currSchool.getId()%>'><i class='material-icons'>edit</i></a>
                                </sec:authorize>
                            </div>
                        </figcaption>
                    </figure>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>


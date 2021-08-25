<%@ page import="org.example.entities.Role" %>
<%@ page import="org.example.entities.Teacher" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>User page</title>
        <link rel="icon" type="img/png" href="../images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="../css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div class="box" align="left">
                <h2>User page</h2>
                <div class="box-index">
                    <div class="user-div">
                        <img src="images/user.png" class="user-img">
                    </div>
                    <div class="user-data">
                        <p>ID: <%=currUser.getId()%></p>
                        <p>Username: <%=currUser.getUsername()%></p>
                        <p>
                            Roles:
                            <%
                                for (Role role: currUser.getRoles()) {
                            %>
                                    <%=role.getName() + " "%>
                            <%
                                }
                            %>
                        </p>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <%
                                Teacher teacher = (Teacher) request.getAttribute("teacher");
                            %>
                            <p>Name:<%=teacher.getName()%></p>
                            <p>Position:<%=teacher.getPosition()%></p>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('PUPIL')">
                            <%
                                Pupil pupil = (Pupil) request.getAttribute("pupil");
                            %>
                            <p>Name:<%=pupil.getName()%></p>
                            <p>Class:<%=pupil.getPupilClass().getName()%></p>
                        </sec:authorize>
                        <p><a href="editUser/<%=currUser.getId()%>">Change username or password</a></p>
                    </div>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>

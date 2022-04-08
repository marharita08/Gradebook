<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="org.example.entities.Role" %>
<%@ page import="org.example.entities.Teacher" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ page import="org.example.entities.User" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
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
        <%
            User user = (User)request.getAttribute("user");
            Pupil pupil = (Pupil) request.getAttribute("pupil");
            Teacher teacher = (Teacher) request.getAttribute("teacher");
            Set<Role> roles = (Set<Role>) request.getAttribute("roles");
            String action = (String) request.getAttribute("action");
            int id = user.getId();
            String func = "checkUsername(" + id + ")";
        %>
        <div align="center">
            <div class="box" align="left">
                <h2>User page</h2>
                <div class="box-index">
                    <div class="user-div">
                        <img alt="user image" src="<%=root%>images/user.png" class="user-img">
                    </div>
                    <div class="user-data">
                        <form action="<%=action%>" method="post">
                            <br/>
                            <% if(action.contains(String.valueOf(id))) { %>
                                <label>ID:</label>
                                <input value="<%=user.getId()%>" name="user.id" readonly="readonly"/>
                            <%}%>
                            <p id="placeToShow" class="warning"></p>
                            <label>Username:</label>
                            <input required onkeyup="<%=func%>" name="username" value="<%=user.getUsername()==null?"":user.getUsername()%>"/>
                            <br/>
                            <label>Password:</label>
                            <input required type="password" name="password"/>
                            <label>Roles:</label>
                            <%
                                StringBuilder userRoles = new StringBuilder();
                                for (Role role: user.getRoles()) {
                                    userRoles.append(role.getName());
                                }
                            %>
                            <sec:authorize access="hasAnyAuthority('PUPIL', 'TEACHER')">
                            <input readonly value="<%=userRoles.toString()%>"/>
                            </sec:authorize>
                            <sec:authorize access="hasAuthority('ADMIN')">
                                <%for (Role role:roles) {
                                    String roleName = role.getName();
                                %>
                                    <input type="checkbox" id="<%="ch" + roleName%>" onclick="roleDiv('<%=roleName%>')"
                                           value="<%=role.getId()%>" name="<%=roleName%>" <%=user.hasRole(roleName)?"checked":""%>/>
                                    <label for="<%=roleName%>"><%=roleName%></label>
                                <%}%>
                            </sec:authorize>
                            <div id="divTEACHER" <%=!user.hasRole("TEACHER")?"style=\"display:none\"":""%>>
                                <input type="hidden" value="<%=user.getId()%>" name="teacher.id"/>
                                <label>Name:</label>
                                <input id="teacher.name" name="teacher.name" value="<%=teacher.getName()==null?"":teacher.getName()%>"
                                        <%=!currUser.hasRole("ADMIN")?"readonly":""%> <%=user.hasRole("TEACHER")?"required":""%>/>
                                <br/>
                                <label>Position:</label>
                                <input name="position" value="<%=teacher.getPosition()==null?"":teacher.getPosition()%>"
                                        <%=!currUser.hasRole("ADMIN")?"readonly":""%>/>
                            </div>
                            <div id="divPUPIL" <%=!user.hasRole("PUPIL")?"style=\"display:none\"":""%>>
                                <input type="hidden" value="<%=user.getId()%>" name="pupil.id"/>
                                <label>Name:</label>
                                <input id="pupil.name" value="<%=pupil.getName()==null?"":pupil.getName()%>" name="pupil.name"
                                        <%=!currUser.hasRole("ADMIN")?"readonly":""%>  <%=user.hasRole("TEACHER")?"required":""%>/>
                                <br/>
                                <% if(pupil.getPupilClass() != null) {%>
                                    <label>Class:</label>
                                    <sec:authorize access="hasAnyAuthority('PUPIL', 'TEACHER')">
                                    <input value="<%=pupil.getPupilClass().getName()%> readonly"/>
                                    </sec:authorize>
                                <%}%>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <label>Class:</label>
                                    <select name="pupil.pupilClass.id">
                                        <option value="0">-</option>
                                        <%
                                            for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("list")) {
                                                String selected = "";
                                                if(pupil.getPupilClass() != null) {
                                                    if(pupil.getPupilClass().getId() == pupilClass.getId()) {
                                                        selected = "selected='selected'";
                                                    }
                                                }
                                        %>
                                        <option value="<%=pupilClass.getId()%>" <%=selected%>>
                                            <%=pupilClass.getName()%>
                                        </option>
                                        <%
                                            }
                                        %>
                                    </select>
                                </sec:authorize>
                            </div>
                            <button type="submit">Save</button><br/><br/>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/checkUsername.js"%>
        function roleDiv(role) {
            var checkBox = document.getElementById("ch" + role);
            var div = document.getElementById("div" + role);
            var name = document.getElementById(role.toLowerCase() + ".name");
            if (checkBox.checked === true){
                div.style.display = "block";
                name.setAttribute("required", "");
            } else {
                div.style.display = "none";
                name.removeAttribute("required");
            }
        }
    </script>
</html>

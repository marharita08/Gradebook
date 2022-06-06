<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="org.example.entities.Role" %>
<%@ page import="org.example.entities.Teacher" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ page import="org.example.entities.User" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>Сторінка користувача</title>
        <link rel="icon" type="img/png" href="../images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="../css/style.css"%></style>
        <meta charset="utf-8">
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
            String func = "checkUsername(" + id + "," + user.getDbName() + ")";
        %>
        <div align="center">
            <div class="box" align="left">
                <br/>
                <ul class="breadcrumb"><%=request.getAttribute("crumbs")%></ul>
                <h2>Сторінка користувача</h2>
                <div class="box-index">
                    <div class="user-div">
                        <img
                            alt="user image"
                            src='<%=root + (user.getPhoto() != null ? user.getPhoto() : "images/user.png")%>'
                            class="user-img"
                            id="avatar"
                        >
                        <%
                            if (user.getId() == currUser.getId()) {
                        %>
                        <br/><br/>
                        <form enctype="multipart/form-data" method="post" action="<%=root%>user/photo">
                            <label class="bg-primary button" id="imgBtn">
                                <div class="inline"><i class='material-icons'>add_a_photo</i></div>
                                <div class="inline">Змінити фото</div>
                                <input type="file" name="file" accept="image/*" id="imgInp" style="display:none"/>
                            </label>
                            <br/><br/>
                            <button class="bg-primary" style="display:none" id="imgSave" type="submit">
                                <div class="inline"><i class='material-icons'>save</i></div>
                                <div class="inline">Зберегти фото</div>
                            </button>
                            <button class="bg-primary" style="display:none" id="imgDel" type="reset">
                                <div class="inline"><i class='material-icons'>delete</i></div>
                                <div class="inline">Видалити фото</div>
                            </button>
                        </form>
                        <%
                            }
                        %>
                    </div>
                    <div class="user-data card" style="width: 60%" align="center">
                        <form action="<%=action%>" method="post" accept-charset="UTF-8">
                            <sec:csrfInput />
                            <input value="<%=user.getPhoto()%>" name="photo" type="hidden"/>
                            <br/>
                            <% if(action.contains(String.valueOf(id))) { %>
                                <div class="row">
                                    <div class="col-25">
                                        <label>ID</label>
                                    </div>
                                    <div class="col-75">
                                        <input value="<%=user.getId()%>" name="user.id" readonly="readonly" type="text"/>
                                    </div>
                                </div>
                            <%}%>
                            <p id="placeToShow" class="warning"></p>
                            <div class="row">
                                <div class="col-25">
                                    <label>Ім<span>&#39;</span>я користувача</label>
                                </div>
                                <div class="col-75">
                                    <input required onkeyup="<%=func%>" name="username" id="username"
                                           value="<%=user.getUsername()==null?"":user.getUsername()%>" type="text"/>
                                    <br/>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-25">
                                    <label>Пароль</label>
                                </div>
                                <div class="col-75">
                                    <input type="password" name="password" <%=user.getUsername() == null ? "required" : ""%>/>
                                    <br/>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-25">
                                    <label>Ролі</label>
                                </div>
                                <div class="col-75">
                                    <sec:authorize access="!hasAuthority('ADMIN')">
                                        <%
                                            StringBuilder userRoles = new StringBuilder();
                                            for (Role role: user.getRoles()) {
                                                userRoles.append(role.getName());
                                            }
                                        %>
                                        <input readonly value="<%=userRoles.toString()%>" type="text"/>
                                    </sec:authorize>
                                    <sec:authorize access="hasAuthority('ADMIN')">
                                        <%for (Role role:roles) {
                                            String roleName = role.getName();
                                        %>
                                        <input type="checkbox" id="<%="ch" + roleName%>" onclick="roleDiv('<%=roleName%>')"
                                               value="<%=role.getId()%>" name="<%=roleName%>" <%=user.hasRole(roleName)?"checked":""%>/>
                                        <label for="<%=roleName%>"><%=roleName%></label>
                                        <%}%>
                                        <br/>
                                    </sec:authorize>
                                </div>
                            </div>
                            <div id="divTEACHER" <%=!user.hasRole("TEACHER")?"style=\"display:none\"":""%>>
                                <input type="hidden" value="<%=user.getId()%>" name="teacher.id"/>
                                <div class="row">
                                    <div class="col-25">
                                        <label>ПІБ</label>
                                    </div>
                                    <div class="col-75">
                                        <input id="teacher.name" name="teacher.name" value="<%=teacher.getName()==null?"":teacher.getName()%>"
                                                <%=!currUser.hasRole("ADMIN")?"readonly":""%> <%=user.hasRole("TEACHER")?"required":""%> type="text"/>
                                        <br/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-25">
                                        <label>Посада</label>
                                    </div>
                                    <div class="col-75">
                                        <input name="position" value="<%=teacher.getPosition()==null?"":teacher.getPosition()%>"
                                                <%=!currUser.hasRole("ADMIN")?"readonly":""%> type="text"/>
                                    </div>
                                </div>
                            </div>
                            <div id="divPUPIL" <%=!user.hasRole("PUPIL")?"style=\"display:none\"":""%>>
                                <input type="hidden" value="<%=user.getId()%>" name="pupil.id"/>
                                <div class="row">
                                    <div class="col-25">
                                        <label>ПІБ</label>
                                    </div>
                                    <div class="col-75">
                                        <input id="pupil.name" value="<%=pupil.getName()==null?"":pupil.getName()%>" name="pupil.name"
                                                <%=!currUser.hasRole("ADMIN")?"readonly":""%>  <%=user.hasRole("PUPIL")?"required":""%> type="text"/>
                                        <br/>
                                    </div>
                                </div>
                                <% if(pupil.getPupilClass() != null) {%>
                                    <sec:authorize access="!hasAuthority('ADMIN')">
                                        <div class="row">
                                            <div class="col-25">
                                                <label>Клас</label>
                                            </div>
                                            <div class="col-75">
                                                <input value="<%=pupil.getPupilClass().getName()%>" readonly type="text"/>
                                            </div>
                                        </div>
                                    </sec:authorize>
                                <%}%>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <div class="row">
                                        <div class="col-25">
                                            <label>Клас</label>
                                        </div>
                                        <div class="col-75">
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
                                        </div>
                                    </div>
                                </sec:authorize>
                            </div>
                            <button onclick="history.back()" type="button" class="bg-primary">
                                <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                                <div class="inline">Назад</div>
                            </button>
                            <button type="submit" class="bg-primary" id="save">
                                <div class="inline"><i class='material-icons'>save</i></div>
                                <div class="inline">Зберегти</div>
                            </button>
                            <br/><br/>
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
        imgInp.onchange = evt => {
          const [file] = imgInp.files
          if (file) {
            imgSave.style.display = "block";
            imgBtn.style.display = "none";
            imgDel.style.display = "block";
            avatar.src = URL.createObjectURL(file);
          }
        }
        imgDel.onclick = evt => {
            imgSave.style.display = "none";
            imgBtn.style.display = "block";
            imgDel.style.display = "none";
            avatar.src = '<%=root + (user.getPhoto() != null ? user.getPhoto() : "images/user.png")%>';
        }
    </script>
</html>

<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.*" %>
<%@ page import="com.google.gson.Gson" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Список деталей предметів</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
        <style><%@include file="../css/style.css"%></style>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <ul class="breadcrumb"><%=request.getAttribute("crumbs")%></ul>
                <h2 align="center"><%=request.getAttribute("header")%></h2>
                <%
                    int pageNum = (int)request.getAttribute("pageNum");
                    String pagination = (String) request.getAttribute("pagination");
                    boolean isAdmin = currUser.hasRole("ADMIN");
                    boolean isTeacher = currUser.hasRole("TEACHER");
                    List<SubjectDetails> list = (List<SubjectDetails>)request.getAttribute("list");
                    PupilClass pupilClass = (PupilClass) request.getAttribute("pupilClass");
                    int colspan = 4;
                    if (isTeacher) {
                        colspan ++;
                    }
                    if (isAdmin) {
                        colspan += 4;
                    }
                    String entity = "'subject-details'";
                %>
                <ul class="pagination"><%=pagination%></ul>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                            <th>Семестр</th>
                        </sec:authorize>
                        <th>Клас</th>
                        <th>Вчитель</th>
                        <th>Предмет</th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th></th>
                            <th></th>
                        </sec:authorize>
                        <th></th>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <th></th>
                        </sec:authorize>
                        <th></th>
                    </tr>
                    <%
                        if (pageNum == 1) {
                            int i = 0;
                    %>
                            <tr>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th>
                                        <input type="text"
                                               id="id"
                                               onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                               class="search-slim"
                                               placeholder="Пошук...">
                                    </th>
                                    <th>
                                        <input type="text"
                                               id="semester"
                                               onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                               class="search"
                                               placeholder="Пошук...">
                                    </th>
                                </sec:authorize>
                                <th>
                                    <input type="text"
                                           id="class"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search-slim"
                                           placeholder="Пошук...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="teacher"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="subject"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th></th>
                                    <th></th>
                                </sec:authorize>
                                <th></th>
                                <sec:authorize access="hasAuthority('TEACHER')">
                                    <th></th>
                                </sec:authorize>
                                <th></th>
                            </tr>
                    <%
                        }
                    %>
                    <tbody id="placeToShow">
                    </tbody>
                </table>
                <br/>
                <button onclick='location.href="<%=root%>main"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Меню</div>
                </button>
                <button onclick=history.back() class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Назад</div>
                </button>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <button onclick='location.href="<%=root%>subject-detail"' class="bg-primary">
                        <div class="inline"><i class='material-icons'>post_add</i></div>
                        <div class="inline">Додати</div>
                    </button>
                </sec:authorize>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        function showTable(obj) {
            var html = [];
            if (!obj.length) {
                html.push(
                    "<tr class='card'>",
                    "<td colspan='<%=colspan%>'>Список деталей предметів пустий</td>",
                    "</tr>"
                );
            } else {
                for (let i in obj) {
                    var id = obj[i].id;
                    html.push("<tr class='card'>");
                    if (<%=isAdmin%>) {
                        html.push(
                            "<td>", id, "</td>",
                            "<td>",
                            "<div class='inline'>", "<i class='material-icons'>calendar_month</i>", "</div>",
                            "<div class='inline'>", obj[i].semester.schoolYear.name, obj[i].semester.name, "</div>",
                            "</td>"
                        );
                    }
                    html.push(
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>group</i>", "</div>",
                        "<div class='inline'>", obj[i].pupilClass.name, "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'>",
                        "<img alt='avatar'",
                        "src='<%=root%>"
                    );
                    if (obj[i].teacher.photo != null) {
                        html.push (obj[i].teacher.photo, "'");
                    } else {
                        html.push ("images/user.png'");
                    }
                    html.push (
                        "class='avatar-30'>",
                        "</div>",
                        "<div class='inline'>"
                    );
                    if (obj[i].teacher != null) {
                        html.push(obj[i].teacher.name);
                    } else {
                        html.push("-");
                    }
                    html.push(
                        "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>import_contacts</i>", "</div>",
                        "<div class='inline'>", obj[i].subject.name, "</div>",
                        "</td>"
                    );
                    if (<%=isAdmin%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>subject-detail/", id, "'><i class='material-icons'>edit</i></a>",
                            "</td><td><a>",
                            "<form action='<%=root%>subject-detail/", id, "/delete' method=post>",
                            '<sec:csrfInput />',
                            "<input type='hidden' value='<%=pageNum%>' name='page'/>",
                            "<button type='submit'>",
                            "<i class='material-icons'>delete</i>",
                            "</button>",
                            "</form></a></td>"
                        );
                    }
                    html.push(
                        "<td>",
                        "<a href='<%=root%>subject-details/", id, "/themes'>теми</a>",
                        "</td>"
                    );
                    if (<%=isTeacher%>) {
                        html.push(
                            "<td>"
                        );
                        if (obj[i].teacher != null && obj[i].teacher.id === <%=currUser.getId()%>) {
                            html.push(
                                "<td>",
                                "<a href='<%=root%>subject-details/", id, "/theme'>додати тему</a>",
                                "</td>"
                            );
                        }
                        html.push(
                            "</td>"
                        );
                    }
                    html.push(
                        "<td>"
                    );
                    if (obj[i].pupilClass.id === <%=pupilClass == null ? 0 : pupilClass.getId()%>
                        || <%=isAdmin%> || <%=isTeacher%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>subject-details/", id, "/marks?page=1'>оцінки</a>",
                            "</td>",
                        );
                    }
                    html.push(
                        "</td>",
                        "</tr>"
                    );
                }
            }
            document.getElementById("placeToShow").innerHTML = html.join("");
        }
        <%@include file="../js/search.js"%>
        window.onload = function load() {
            showTable(<%=new Gson().toJson(list)%>);
        }
        <%@include file="../js/filter.js"%>
    </script>
</html>

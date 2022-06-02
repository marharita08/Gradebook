<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Semester" %>
<%@ page import="com.google.gson.GsonBuilder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Список семестрів</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <style><%@include file="../css/style.css"%></style>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
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
                    boolean isPupil = currUser.hasRole("PUPIL");
                    List<Semester> list = (List<Semester>)request.getAttribute("list");
                    int colspan = isAdmin ? 7 : 5;
                    String entity = "'semesters'";
                %>
                <ul class="pagination"><%=pagination%></ul>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                        </sec:authorize>
                        <th>Назва</th>
                        <th>Навчальний рік</th>
                        <th>Дата початку</th>
                        <th>Дата закінчення</th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th></th>
                            <th></th>
                        </sec:authorize>
                        <sec:authorize access="hasAnyAuthority('TEACHER', 'PUPIL')">
                            <th></th>
                        </sec:authorize>
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
                                </sec:authorize>
                                <th>
                                    <input type="text"
                                           id="name"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="schoolYear"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="startDate"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="endDate"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th></th>
                                    <th></th>
                                </sec:authorize>
                                <sec:authorize access="hasAnyAuthority('TEACHER', 'PUPIL')">
                                    <th></th>
                                </sec:authorize>
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
                <button onclick='history.back()' class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Назад</div>
                </button>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <button onclick='location.href="<%=root%>semester"' class="bg-primary">
                        <div class="inline"><i class='material-icons'>edit_calendar</i></div>
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
                    "<td colspan='<%=colspan%>'>Список семестрів пустий</td>",
                    "</tr>"
                );
            } else {
                for (let i in obj) {
                    html.push("<tr class='card'>");
                    var id = obj[i].id;
                    var startDate = new Date(obj[i].startDate);
                    var endDate = new Date(obj[i].endDate);
                    if (<%=isAdmin%>) {
                        html.push("<td>", id, "</td>");
                    }
                    html.push(
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>date_range</i>", "</div>",
                        "<div class='inline'>", obj[i].name, "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>calendar_month</i>", "</div>",
                        "<div class='inline'>", obj[i].schoolYear.name, "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>today</i>", "</div>",
                        "<div class='inline'>",
                        ("0" + startDate.getDate()).slice(-2), ".", ("0" + (startDate.getMonth() + 1)).slice(-2), ".",
                        startDate.getFullYear(),
                        "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>event</i>", "</div>",
                        "<div class='inline'>",
                        ("0" + endDate.getDate()).slice(-2), ".", ("0" + (endDate.getMonth() + 1)).slice(-2), ".",
                        endDate.getFullYear(),
                        "</div>",
                        "</td>",
                    );
                    if (<%=isAdmin%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>semester/", id, "'><i class='material-icons'>edit</i></a>",
                            "</td><td><a>",
                            "<form action='<%=root%>semester/", id, "/delete' method=post>",
                            '<sec:csrfInput />',
                            "<input type='hidden' value='<%=pageNum%>' name='page'/>",
                            "<button type='submit'>",
                            "<i class='material-icons'>delete</i>",
                            "</button>",
                            "</form></a></td>"
                            );
                    }
                    if (<%=isTeacher%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>semester/", id, "/teacher/<%=currUser.getId()%>'>",
                            "мої предмети",
                            "</a>",
                            "</td>"
                        );
                    }
                    if (<%=isPupil%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>semester/", id, "/pupil/<%=currUser.getId()%>'>",
                            "мої предмети",
                            "</a>",
                            "</td>"
                        );
                    }
                    html.push("</tr>");
                }
            }
            document.getElementById("placeToShow").innerHTML = html.join("");
        }
        <%@include file="../js/search.js"%>
        window.onload = function load() {
            showTable(<%=new GsonBuilder().setDateFormat("yyyy-MM-dd").create().toJson(list)%>);
        }
        <%@include file="../js/filter.js"%>
    </script>
</html>

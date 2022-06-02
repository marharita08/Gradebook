<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.User" %>
<%@ page import="org.example.controllers.PaginationController" %>
<%@ page import="com.google.gson.Gson" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Список користувачів</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <style><%@include file="../css/style.css"%></style>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link rel="icon" type="img/png" href="images/icon.png">
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <ul class="breadcrumb"><%=request.getAttribute("crumbs")%></ul>
                <h2 align="center">Список користувачів</h2>
                <%
                    PaginationController paginationController = (PaginationController) request.getAttribute("pagination");
                    int pageNum = paginationController.getCurrentPageNumber();
                    List<User> list = (List<User>)request.getAttribute("list");
                %>
                <ul class="pagination"><%=paginationController.makePagingLinks("users")%></ul>
                <table id="myTable">
                    <tr>
                        <th>ID</th>
                        <th>Ім<span>&#39;</span>я користувача</th>
                        <th>Ролі</th>
                        <th></th>
                        <th></th>
                    </tr>
                    <%
                        if (pageNum == 1) {
                    %>
                            <tr>
                                <th>
                                    <input type="text"
                                           id="id"
                                           onkeyup="search(id, 'user')"
                                           class="search-slim"
                                           placeholder="Пошук...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="username"
                                           onkeyup="search(id, 'user')"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="roles"
                                           onkeyup="search(id, 'user')"
                                           class="search"
                                           placeholder="Пошук...">
                                </th>
                                <th></th>
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
                <button onclick='location.href="user"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>person_add</i></div>
                    <div class="inline">Додати</div>
                </button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        function showTable(obj) {
            var html = [];
            for (let i in obj) {
                var id = obj[i].id;
                html.push (
                    "<tr class='card'>",
                    "<td>", id, "</td>",
                    "<td>",
                    "<div class='inline'>", "<i class='material-icons'>person</i>", "</div>",
                    "<div class='inline'>", obj[i].username, "</div>",
                    "</td>",
                    "<td>",
                    "<div class='inline'>", "<i class='material-icons'>group</i>", "</div>",
                    "<div class='inline'>"
                );
                var roles = obj[i].roles;
                for (let j in roles) {
                    html.push("<p>", roles[j].name, "</p>");
                }
                html.push(
                    "</div>",
                    "</td><td>",
                    "</td><td>",
                    "<a href='<%=root%>user/", id, "'><i class='material-icons'>edit</i></a>",
                    "</td><td>"
                );
                if (id !== 0) {
                    html.push(
                        "<a><form action='<%=root%>user/", id, "/delete' method=post>",
                        '<sec:csrfInput />',
                        "<input type='hidden' value='<%=pageNum%>' name='page'/>",
                        "<button type='submit'>",
                        "<i class='material-icons'>delete</i>",
                        "</button>",
                        "</form></a>"
                    );
                }
                html.push(
                    "</td>",
                    "</tr>"
                )
            }
            document.getElementById("placeToShow").innerHTML = html.join("");
        }
        <%@include file="../js/search.js"%>
        window.onload = function load() {
            showTable(<%=new Gson().toJson(list)%>);
        }
    </script>
</html>

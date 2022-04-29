<%@ page import="org.example.entities.Teacher" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.gson.Gson" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Teacher List</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <style><%@include file="../css/style.css"%></style>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <h2 align="center"><%=request.getAttribute("header")%></h2>
                <%
                    int pageNum = (int)request.getAttribute("pageNum");
                    String pagination = (String) request.getAttribute("pagination");
                    boolean isAdmin = currUser.hasRole("ADMIN");
                    List<Teacher> list = (List<Teacher>)request.getAttribute("list");
                    int colspan = isAdmin ? 6 : 3;
                    String entity = "'teachers'";
                %>
                <ul class="pagination"><%=pagination%></ul>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                        </sec:authorize>
                        <th>Name</th>
                        <th>Position</th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th></th>
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
                                               placeholder="Search...">
                                    </th>
                                </sec:authorize>
                                <th>
                                    <input type="text"
                                           id="name"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search-middle"
                                           placeholder="Search...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="position"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i + ")" : "search(id," + entity +")"%>"
                                           class="search-middle"
                                           placeholder="Search...">
                                </th>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th></th>
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
                <button onclick='location.href="<%=root%>index.jsp"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Menu</div>
                </button>
                <button onclick='history.back()' class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Back</div>
                </button>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <button onclick='location.href="<%=root%>user"' class="bg-primary">
                        <div class="inline"><i class='material-icons'>person_add</i></div>
                        <div class="inline">Add</div>
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
                    "<td colspan='<%=colspan%>'>List of teachers is empty</td>",
                    "</tr>"
                );
            } else {
                for (let i in obj) {
                    var id = obj[i].id;
                    html.push("<tr class='card'>");
                    if (<%=isAdmin%>) {
                        html.push("<td>", id, "</td>");
                    }
                    html.push(
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>person</i>", "</div>",
                        "<div class='inline'>", obj[i].name, "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>badge</i>", "</div>",
                        "<div class='inline'>", obj[i].position, "</div>",
                        "</td>"
                    );
                    if (<%=isAdmin%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>user/", id, "'><i class='material-icons'>edit</i></a>",
                            "</td><td>",
                            "<a href='<%=root%>teacher/", id, "delete?page=1'><i class='material-icons'>delete</i></a>",
                            "</td>",
                        );
                    }
                    html.push("<td>",
                        "<a href='<%=root%>teacher/", id, "/subject-details'>view subjects</a>",
                        "</td>",
                        "</tr>"
                    );
                }
            }
            document.getElementById("placeToShow").innerHTML = html.join("");
        }
        <%@include file="../js/search.js"%>
        <%@include file="../js/filter.js"%>
        window.onload = function load() {
            showTable(<%=new Gson().toJson(list)%>);
        }
    </script>
</html>

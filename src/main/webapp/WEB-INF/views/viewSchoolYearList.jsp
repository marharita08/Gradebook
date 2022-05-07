<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.SchoolYear" %>
<%@ page import="com.google.gson.GsonBuilder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>School Years List</title>
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
                    List<SchoolYear> list = (List<SchoolYear>)request.getAttribute("list");
                    int colspan = isAdmin ? 6 : 3;
                    String entity = "'years'";
                %>
                <ul class="pagination"><%=pagination%></ul>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                        </sec:authorize>
                        <th>Name</th>
                        <th>Start date</th>
                        <th>End date</th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th></th>
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
                                               placeholder="Search...">
                                    </th>
                                </sec:authorize>
                                <th>
                                    <input type="text"
                                           id="name"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Search...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="startDate"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Search...">
                                </th>
                                <th>
                                    <input type="text"
                                           id="endDate"
                                           onkeyup="<%=pagination.equals("")?"filter(id," + i + ")" : "search(id," + entity +")"%>"
                                           class="search"
                                           placeholder="Search...">
                                </th>
                                <sec:authorize access="hasAuthority('ADMIN')">
                                    <th></th>
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
                <button onclick='location.href="<%=root%>index.jsp"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Menu</div>
                </button>
                <button onclick='history.back()' class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Back</div>
                </button>
                <sec:authorize access="hasAuthority('ADMIN')">
                    <button onclick='location.href="<%=root%>year"' class="bg-primary">
                        <div class="inline"><i class='material-icons'>edit_calendar</i></div>
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
                    "<td colspan='<%=colspan%>'>List of school years is empty</td>",
                    "</tr>"
                );
            } else {
                for (let i in obj) {
                    var id = obj[i].id;
                    var startDate = new Date(obj[i].startDate);
                    var endDate = new Date(obj[i].endDate);
                    html.push("<tr class='card'>");
                    if (<%=isAdmin%>) {
                        html.push("<td>", id, "</td>");
                    }
                    html.push(
                        "<td>",
                        "<div class='inline'><i class='material-icons'>calendar_month</i></div>",
                        "<div class='inline'>", obj[i].name, "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'><i class='material-icons'>today</i></div>",
                        "<div class='inline'>",
                        ("0" + startDate.getDate()).slice(-2), ".", ("0" + (startDate.getMonth() + 1)).slice(-2), ".",
                        startDate.getFullYear(),
                        "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'><i class='material-icons'>event</i></div>",
                        "<div class='inline'>",
                        ("0" + endDate.getDate()).slice(-2), ".", ("0" + (endDate.getMonth() + 1)).slice(-2), ".",
                        endDate.getFullYear(),
                        "</div>",
                        "</td>"
                    );
                    if (<%=isAdmin%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>year/", id, "'><i class='material-icons'>edit</i></a>",
                            "</td><td>",
                            "<a href='<%=root%>year/", id,
                            "/delete?page=<%=pageNum%>'><i class='material-icons'>delete</i></a>",
                            "</td>"
                        );
                    }
                    html.push("</tr>");
                }
            }
            document.getElementById("placeToShow").innerHTML = html.join("");
        }
        <%@include file="../js/search.js"%>
        <%@include file="../js/filter.js"%>
        window.onload = function load() {
            showTable(<%=new GsonBuilder().setDateFormat("yyyy-MM-dd").create().toJson(list)%>);
        }
    </script>
</html>

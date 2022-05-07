<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="com.google.gson.Gson" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Class List</title>
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
                    List<PupilClass> list = (List<PupilClass>) request.getAttribute("list");
                    int colspan = 3;
                    String entity = "'classes'";
                %>
                <ul class="pagination"><%=pagination%></ul>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                        </sec:authorize>
                        <th>Grade</th>
                        <th>Name</th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th></th>
                            <th></th>
                            <%colspan += 3;%>
                        </sec:authorize>
                        <sec:authorize access="hasAnyAuthority('ADMIN', 'TEACHER')">
                            <th></th>
                            <%colspan ++;%>
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
                                   id="grade"
                                   onkeyup="<%=pagination.equals("")?"filter(id," + i++ + ")" : "search(id," + entity +")"%>"
                                   class="search-slim"
                                   placeholder="Search...">
                        </th>
                        <th>
                            <input type="text"
                                   id="name"
                                   onkeyup="<%=pagination.equals("")?"filter(id," + i + ")" : "search(id," + entity +")"%>"
                                   class="search-slim"
                                   placeholder="Search...">
                        </th>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th></th>
                            <th></th>
                        </sec:authorize>
                        <sec:authorize access="hasAnyAuthority('ADMIN', 'TEACHER')">
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
                    <button onclick='location.href="<%=root%>class"' class="bg-primary">
                        <div class="inline"><i class='material-icons'>group_add</i></div>
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
                    "<td colspan='<%=colspan%>'>List of classes is empty</td>",
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
                        "<div class='inline'>", "<i class='material-icons'>school</i>", "</div>",
                        "<div class='inline'>", obj[i].grade, "</div>",
                        "</td>",
                        "<td>",
                        "<div class='inline'>", "<i class='material-icons'>group</i>", "</div>",
                        "<div class='inline'>", obj[i].name, "</div>",
                        "</td>",
                    );
                    if (<%=isAdmin%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>class/", id, "'><i class='material-icons'>edit</i></a>",
                            "</td><td>",
                            "<a href='<%=root%>class/", id,
                            "delete?page=<%=pageNum%>'><i class='material-icons'>delete</i></a>",
                            "</td>"
                        );
                    }
                    if (<%=isAdmin||isTeacher%>) {
                        html.push(
                            "<td>",
                            "<a href='<%=root%>class/", id, "/pupils'>view pupils</a>",
                            "</td>"
                        );
                    }
                    html.push(
                        "<td>",
                        "<a href='<%=root%>class/", id, "/subject-details'>view subjects</a>",
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
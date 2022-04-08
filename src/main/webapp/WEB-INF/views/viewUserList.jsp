<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.User" %>
<%@ page import="org.example.entities.Role" %>
<%@ page import="org.example.controllers.PaginationController" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>View User List</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <style><%@include file="../css/style.css"%></style>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link rel="icon" type="img/png" href="images/icon.png">
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <h2 align="center">User List: </h2>
                <%
                    PaginationController paginationController = (PaginationController) request.getAttribute("pagination");
                    int pageNum = paginationController.getCurrentPageNumber();
                %>
                <ul class="pagination"><%=paginationController.makePagingLinks("users")%></ul>
                <table id="myTable">
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Roles</th>
                        <th>EDIT</th>
                        <th>DELETE</th>
                    </tr>
                    <%
                        if (pageNum == 1) {
                    %>
                            <tr>
                                <th><input type="text" id="id" onkeyup="search(id)" class="search-slim"></th>
                                <th><input type="text" id="username" onkeyup="search(id)" class="search"></th>
                                <th><input type="text" id="roles" onkeyup="search(id)" class="search"></th>
                                <th></th>
                                <th></th>
                            </tr>
                    <%
                        }
                    %>
                    <tbody id="placeToShow">
                        <%
                            for (User user:(List<User>)request.getAttribute("list")) {
                        %>
                                <tr>
                                    <td><%=user.getId()%></td>
                                    <td><%=user.getUsername()%></td>
                                    <td>
                                        <%
                                            for (Role role:user.getRoles()) {
                                        %>
                                                <p>
                                                    <%=role.getName()%>
                                                </p>
                                        <%
                                            }
                                        %>
                                    </td>
                                    <td><a href="user/<%=user.getId()%>">Edit</a></td>
                                    <td>
                                        <%if (user.getId() != 0) {%>
                                            <a href="user/<%= user.getId()%>/delete?page=<%=pageNum%>">Delete</a>
                                        <%}%>
                                    </td>
                                </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
                <br/>
                <button onclick='location.href="index.jsp"'>Menu</button>
                <button onclick='location.href="user"'>Add</button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        var request = new XMLHttpRequest();
        function search(param) {
            var val = document.getElementById(param).value;
            var url = "user/search?val=" + val + "&param=" + param;
            try {
                request.onreadystatechange = function () {
                    if (request.readyState === 4) {
                        var obj = JSON.parse(request.responseText);
                        var result = "";
                        for (let i in obj) {
                            var id = obj[i].id;
                            result += "<tr>";
                            result += "<td>" + id + "</td>";
                            result += "<td>" + obj[i].username + "</td>";
                            result += "<td>";
                            var roles = obj[i].roles;
                            for (let j in roles) {
                                result += "<p>";
                                result += roles[j].name;
                                result += "</p>";
                            }
                            result += "</td><td>";
                            result += "<a href=\"user/" + id + "\">Edit</a>";
                            result += "</td><td>";
                            if (id !== 0) {
                                result += "<a href=\"user/" + id + "/delete?page=1\">Delete</a>";
                            }
                            result += "</td>";
                            result += "</tr>";
                        }
                        document.getElementById("placeToShow").innerHTML = result;
                    }
                }
                request.open("GET", url, true);
                request.send();
            } catch (e) {
                alert("Unable to connect to server");
            }
        }
    </script>
</html>

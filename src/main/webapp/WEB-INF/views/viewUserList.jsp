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
                <ul class="pagination"><%=paginationController.makePagingLinks("viewAllUsers")%></ul>
                <table id="myTable">
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Roles</th>
                        <th>Add role</th>
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
                                                    <%=role.getName() + " "%>
                                                    <%
                                                        if(role.getId()==1 && user.getId()!=1) {
                                                    %>
                                                            <a href="deleteRole/<%=user.getId()%>/<%=role.getId()%>">
                                                                Delete
                                                            </a>
                                                    <%
                                                        }
                                                    %>
                                                </p>
                                        <%
                                            }
                                        %>
                                    </td>
                                    <td><a href="addRole/<%=user.getId()%>/1">ADMIN</a></td>
                                    <td><a href="editUser/<%=user.getId()%>">Edit</a></td>
                                    <td>
                                        <%=user.getId() == 1 ? "" : "<a href=\"deleteUser/"
                                            + user.getId() + "?page=" + pageNum + "\">Delete</a>"%>
                                    </td>
                                </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
                <br/>
                <button onclick='location.href="index.jsp"'>Menu</button>
                <button onclick='location.href="addUser"'>Add</button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        var request = new XMLHttpRequest();
        function search(param) {
            var val = document.getElementById(param).value;
            var url = "searchUsers?val=" + val + "&param=" + param;
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
                                result += roles[j].name + " ";
                                if(roles[j].id === 1 && id !== 1) {
                                    result += "<a href=\"deleteRole/" + id +
                                        "/" + roles[j].id + "\">Delete</a>";
                                }
                                result += "</p>";
                            }
                            result += "</td><td>";
                            result += "<p><a href=\"addRole/" + id + "/1\">ADMIN</a></p>";
                            result += "</td><td>";
                            result += "<a href=\"editUser/" + id + "\">Edit</a>";
                            result += "</td><td>";
                            if (id !== 1) {
                                result += "<a href=\"deleteUser/" + id + "?page=1\">Delete</a>";
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

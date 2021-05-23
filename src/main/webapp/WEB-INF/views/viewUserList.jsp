<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.User" %>
<%@ page import="org.example.entities.Role" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>View User List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<%@include file="header.jsp"%>
<h2 align="center">User List: </h2>
<div align="center">
    <select OnChange="sortTable(value)">
        <option>Sort by</option>
        <option value="0">ID</option>
        <option value="1">Username</option>
        <option value="2">Password</option>
        <option value="3">Roles</option>
    </select><br/>
    <ul class="pagination"><%=request.getAttribute("pagination")%></ul>
    <table id="myTable">
        <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Password</th>
            <th>Roles</th>
            <th>Add role</th>
            <th>EDIT</th>
            <th>DELETE</th>
        </tr>
        <tr>
            <th><input type="text" id="id" onkeyup="filter(id, 0)" class="filters"></th>
            <th><input type="text" id="username" onkeyup="filter(id, 1)" class="filters"></th>
            <th><input type="text" id="password" onkeyup="filter(id, 2)" class="filters"></th>
            <th><input type="text" id="roles" onkeyup="filter(id, 3)" class="filters"></th>
            <th></th>
            <th></th>
            <th></th>
        </tr>

        <% for (User user:(List<User>)request.getAttribute("list")) { %>
        <tr>
            <td><%=user.getId()%></td>
            <td><%=user.getUsername()%></td>
            <td><%=user.getPassword()%></td>
            <td>
                <%for (Role role:user.getRoles()) {%>
                <p>
                <%=role.getName() + " "%>
                    <% if(role.getId()!=1 || user.getId()!=1) {%>
                    <a href="/deleteRole/<%=user.getId()%>/<%=role.getId()%>">Delete</a>
                    <%}%>
                </p>
                <%}%>
            </td>
            <td>
                <%for (Role role:(Set<Role>)request.getAttribute("roles")) {%>
                <p><a href="addRole/<%=user.getId()%>/<%=role.getId()%>"><%=role.getName()%></a></p>
                    <%}%>

            </td>
            <td><a href="editUser/<%=user.getId()%>">Edit</a></td>
            <td><%=user.getId() == 1 ? "" : "<a href=\"deleteUser/" + user.getId() + "\">Delete</a>"%></td>
        </tr>
        <% } %>
    </table>
    <br/>
    <button onclick='location.href="/Gradebook/"'>Menu</button>
    <button onclick='location.href="/Gradebook/addUser"'>Add</button>
</div>
<%@include file="footer.jsp"%>

</body>
<script>
    <%@include file="../js/filterAndSort.js"%>
</script>
</html>

<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 12.04.2021
  Time: 16:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>View Pupil List</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<h2 align="center">Pupil List: </h2>
<div align="center">
    <select OnChange="sortTable(value)">
        <option>Sort by</option>
        <option value="0">ID</option>
        <option value="1">Class</option>
        <option value="2">Name</option>
    </select><br/><br/>
    <table id="myTable">
        <tr>
            <th>ID</th>
            <th>Class</th>
            <th>Name</th>
            <th>EDIT</th>
            <th>DELETE</th>
        </tr>
        <tr>
            <th><input type="text" id="id" onkeyup="filter(id, 0)" class="filters"></th>
            <th><input type="text" id="class" onkeyup="filter(id, 1)" class="filters"></th>
            <th><input type="text" id="name" onkeyup="filter(id, 2)" class="filters"></th>
            <th></th>
            <th></th>
        </tr>

        <% for (Pupil pupil:(List<Pupil>)request.getAttribute("list")) { %>
        <tr>
            <td><%=pupil.getId()%></td>
            <% if(pupil.getPupilClass() != null) { %>
                <td><%=pupil.getPupilClass().getName()%></td>
            <% } else { %>
                <td>-</td>
            <%}%>
            <td><%=pupil.getName()%></td>
            <td><a href="editPupil/<%=pupil.getId()%>">Edit</a></td>
            <td><a href="deletePupil/<%=pupil.getId()%>">Delete</a></td>
        </tr>
        <% } %>
    </table>
    <br/>
    <button onclick='location.href="index.jsp"'>Menu</button>
    <button onclick='location.href="addPupil"'>Add</button>
</div>


</body>
<script>
    function filter(id, index) {
        var input, filter, table, tr, td, i;
        input = document.getElementById(id);
        filter = input.value.toUpperCase();
        table = document.getElementById("myTable");
        tr = table.getElementsByTagName("tr");
        for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[index];
            if (td) {
                if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
    function sortTable(index) {
        var table, rows, switching, i, x, y, shouldSwitch;
        table = document.getElementById("myTable");
        switching = true;
        while (switching) {
            switching = false;
            rows = table.getElementsByTagName("TR");
            for (i = 2; i < (rows.length - 1); i++) {
                shouldSwitch = false;
                x = rows[i].getElementsByTagName("TD")[index];
                y = rows[i + 1].getElementsByTagName("TD")[index];
                if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
            if (shouldSwitch) {
                rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
                switching = true;
            }
        }
    }
</script>
</html>
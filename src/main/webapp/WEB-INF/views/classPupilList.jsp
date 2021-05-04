<%@ page import="org.example.entities.Pupil" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 26.04.2021
  Time: 17:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Pupil list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
    <h2>Pupils of <%=request.getAttribute("class")%> form</h2>
    <table>
    <% for (Pupil pupil:(List<Pupil>)request.getAttribute("list")) { %>
        <tr><td><%=pupil.getName()%></td></tr>
    <% } %>
    </table>
    <br/>
    <button onclick='location.href="../index.jsp"'>Menu</button>
    <button onclick='location.href="../showClassList"'>To class List</button>
</body>
</html>
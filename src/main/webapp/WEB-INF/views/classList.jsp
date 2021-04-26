<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 26.04.2021
  Time: 17:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Class list</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
    <h2>Choose class:</h2>
    <% for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("list")) { %>
        <p><a href="<%=request.getAttribute("path") + "/" + pupilClass.getId()%>"><%=pupilClass.getName()%></a></p>
    <% } %>
    <br/>
    <button onclick='location.href="index.jsp"'>Menu</button>
</body>
</html>

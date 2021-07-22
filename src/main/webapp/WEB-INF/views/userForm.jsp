<%@ page import="org.example.entities.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title><%=request.getAttribute("title")%></title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="icon" type="img/png" href="images/icon.png">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
<div align="center">
    <div align="center" class="box">
<h2 align="center"><%=request.getAttribute("title")%></h2>
<% int id = ((User)request.getAttribute("command")).getId();
    String func = "checkUsername(" + id + ")";%>

    <form:form>
        <br/>
        <p id="placeToShow" class="warning"></p>
        Username:
        <form:input path="username" required="true" onkeyup="<%=func%>" id="username"/><br/><br/>
        Password:
        <form:input path="password" required="true" type="password"/>

        <br/><br/>
        <form:input path="id" type="hidden"/>
        <button onclick="history.back()" type="button">Cancel</button>
        <button formmethod="post" formaction="<%=request.getAttribute("formAction")%>">Save</button><br/><br/>
    </form:form>
</div>
</div>
<%@include file="footer.jsp"%>
</body>
<script>
    <%@include file="../js/checkUsername.js"%>
</script>
</html>

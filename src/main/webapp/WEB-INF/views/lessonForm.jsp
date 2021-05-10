<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="org.example.entities.SubjectDetails" %><%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 13.04.2021
  Time: 17:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title><%=request.getAttribute("title")%></title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<h2 align="center"><%=request.getAttribute("title")%></h2>
<div align="center">
    <form:form>
        <br/>
        Subject:
        <form:select path="subjectDetails.id">
            <% for (SubjectDetails subjectDetails:(List<SubjectDetails>)request.getAttribute("list")) { %>
            <option value="<%=subjectDetails.getId()%>" <%=(int)request.getAttribute("selectedSubjectDetails") == subjectDetails.getId() ? "selected='selected'":""%>>
                <%=subjectDetails.getPupilClass().getName() + " " + subjectDetails.getSubject().getName() + " " + subjectDetails.getTeacher().getName() %>
            </option>
            <% } %>
        </form:select><br/><br/>
        Date:
        <form:input type="date" path="date" required="true"/><br/><br/>
        Topic:
        <form:input path="topic" required="true"/><br/><br/>
        <form:input path="id" type="hidden"/>
        <button onclick="history.back()" type="button">Cancel</button>
        <button formmethod="post" formaction="<%=request.getAttribute("formAction")%>">Save</button><br/><br/>
    </form:form>
</div>
</body>
</html>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
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
<%@include file="header.jsp"%>
<h2 align="center"><%=request.getAttribute("title")%></h2>
<div align="center">
    <form:form>
        <br/>
        Class:
        <form:select path="pupilClass.id">
            <option value="0">-</option>
            <% for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("list")) { %>
                <option value="<%=pupilClass.getId()%>" <%=(int)request.getAttribute("selectedClass") == pupilClass.getId() ? "selected='selected'":""%>>
                    <%=pupilClass.getName()%>
                </option>
            <% } %>
        </form:select><br/><br/>
        Name:
        <form:input path="name" required="true"/><br/><br/>
        <form:input path="id" type="hidden"/>
        <button onclick="history.back()" type="button">Cancel</button>
        <button formmethod="post" formaction="<%=request.getAttribute("formAction")%>">Save</button><br/><br/>
    </form:form>
</div>
<%@include file="footer.jsp"%>
</body>
</html>
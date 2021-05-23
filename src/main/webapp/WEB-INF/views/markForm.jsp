<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Pupil" %>
<%@ page import="org.example.entities.Mark" %>
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
    <p>Teacher:<%=((Mark)request.getAttribute("command")).getLesson().getSubjectDetails().getTeacher().getName()%></p>
    <p>Subject:<%=((Mark)request.getAttribute("command")).getLesson().getSubjectDetails().getSubject().getName()%></p>
    <p>Date:<%=((Mark)request.getAttribute("command")).getLesson().getDate()%></p>
    <form:form>
        <br/>
        Pupil:
        <form:select path="pupil.id">
            <% for (Pupil pupil:(List<Pupil>)request.getAttribute("list")) { %>
            <option value="<%=pupil.getId()%>" <%=(int)request.getAttribute("selectedPupil") == pupil.getId() ? "selected='selected'":""%>>
                <%=pupil.getName()  %>
            </option>
            <% } %>
        </form:select><br/><br/>
        Mark:
        <form:select path="mark">
        <% for (int i = 1; i <= 12; i++) { %>
        <option value="<%=i%>" <%=(int)request.getAttribute("selectedMark") == i ? "selected='selected'":""%>>
            <%=i %>
        </option>
        <% } %>
        </form:select><br/><br/>

        <form:input path="id" type="hidden"/>
        <form:input path="lesson.id" type="hidden"/>
        <button onclick="history.back()" type="button">Cancel</button>
        <button formmethod="post" formaction="<%=request.getAttribute("formAction")%>">Save</button><br/><br/>
    </form:form>
</div>
<%@include file="footer.jsp"%>
</body>
</html>

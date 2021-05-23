<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.PupilClass" %>
<%@ page import="org.example.entities.Teacher" %>
<%@ page import="org.example.entities.Subject" %>
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
            <% for (PupilClass pupilClass:(List<PupilClass>)request.getAttribute("classList")) { %>
            <option value="<%=pupilClass.getId()%>" <%=(int)request.getAttribute("selectedClass") == pupilClass.getId() ? "selected='selected'":""%>>
                <%=pupilClass.getName()%>
            </option>
            <% } %>
        </form:select><br/><br/>

        Teacher:
        <form:select path="teacher.id">
            <option value="0">-</option>
            <% for (Teacher teacher:(List<Teacher>)request.getAttribute("teacherList")) { %>
            <option value="<%=teacher.getId()%>" <%=(int)request.getAttribute("selectedTeacher") == teacher.getId() ? "selected='selected'":""%>>
                <%=teacher.getName()%>
            </option>
            <% } %>
        </form:select><br/><br/>

        Subject:
        <form:select path="subject.id">
            <% for (Subject subject:(List<Subject>)request.getAttribute("subjectList")) { %>
            <option value="<%=subject.getId()%>" <%=(int)request.getAttribute("selectedSubject") == subject.getId() ? "selected='selected'":""%>>
                <%=subject.getName()%>
            </option>
            <% } %>
        </form:select><br/><br/>
        <form:input path="id" type="hidden"/>
        <button onclick="history.back()" type="button">Cancel</button>
        <button formmethod="post" formaction="<%=request.getAttribute("formAction")%>">Save</button><br/><br/>
    </form:form>
</div>
<%@include file="footer.jsp"%>
</body>
</html>
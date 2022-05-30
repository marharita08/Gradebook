<%@ page import="org.example.entities.School" %>
<%
     School school = (School)request.getAttribute("school");
%>
<footer class="bg-primary">
    <sec:authorize access="isAuthenticated()">
        <p><%=school!=null?school.getName():"Електронні журнали для загальноосвітніх шкіл"%></p>
    </sec:authorize>
    <sec:authorize access="!isAuthenticated()">
        <p>Електронні журнали для загальноосвітніх шкіл</p>
    </sec:authorize>
</footer>

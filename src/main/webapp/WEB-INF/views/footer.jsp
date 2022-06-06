<%@ page import="org.example.entities.School" %>
<%
     School school = (School)request.getAttribute("school");
%>
<footer class="bg-primary">
     <p><%=school!=null?school.getName():"Електронні журнали для загальноосвітніх шкіл"%></p>
</footer>
